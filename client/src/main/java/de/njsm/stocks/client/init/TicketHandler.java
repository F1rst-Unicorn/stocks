package de.njsm.stocks.client.init;

import de.njsm.stocks.client.config.Configuration;
import de.njsm.stocks.client.data.Ticket;
import de.njsm.stocks.client.exceptions.InitialisationException;
import de.njsm.stocks.client.exceptions.PrintableException;
import de.njsm.stocks.client.network.HttpClientFactory;
import de.njsm.stocks.client.network.TcpHost;
import de.njsm.stocks.client.network.sentry.SentryManager;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketHandler {

    private final LinkedList<Process> waitList;
    private final TcpHost ticketHost;

    static final String CA_FILE_PATH = Configuration.STOCKS_HOME + "/ca.cert.pem";
    static final String INTERMEDIATE_FILE_PATH = Configuration.STOCKS_HOME + "/intermediate.cert.pem";
    static final String CSR_FILE_PATH = Configuration.STOCKS_HOME + "/client.csr.pem";
    static final String CERT_FILE_PATH = Configuration.STOCKS_HOME + "/client.cert.pem";

    TicketHandler (TcpHost ticketHost) {
        waitList = new LinkedList<>();
        this.ticketHost = ticketHost;
    }

    void generateKey(String username, String deviceName, int[] ids) throws Exception {

        String cn = String.format("%s$%d$%s$%d", username, ids[0], deviceName, ids[1]);
        String keyGenCommand = String.format("keytool -genkeypair " +
                        "-dname CN=%s,OU=%s,O=%s " +
                        "-alias %s " +
                        "-keyalg RSA " +
                        "-keysize 4096 " +
                        "-keypass %s " +
                        "-keystore %s " +
                        "-storepass %s ",
                cn,
                "User",
                "stocks",
                "client",
                Configuration.KEYSTORE_PASSWORD,
                Configuration.KEYSTORE_PATH,
                Configuration.KEYSTORE_PASSWORD);
        Process p = Runtime.getRuntime().exec(keyGenCommand);
        InputStream resultStream = p.getInputStream();
        InputStream errorStream = p.getErrorStream();
        IOUtils.copy(resultStream, System.out);
        IOUtils.copy(errorStream, System.out);
        waitList.add(p);
    }

    void generateCsr() throws Exception {
        String getCsrCommand = String.format("keytool -certreq " +
                        "-alias client " +
                        "-file %s " +
                        "-keypass %s " +
                        "-keystore %s " +
                        "-storepass %s ",
                CSR_FILE_PATH,
                Configuration.KEYSTORE_PASSWORD,
                Configuration.KEYSTORE_PATH,
                Configuration.KEYSTORE_PASSWORD);
        Process p = Runtime.getRuntime().exec(getCsrCommand);
        InputStream resultStream = p.getInputStream();
        InputStream errorStream = p.getErrorStream();
        IOUtils.copy(resultStream, System.out);
        IOUtils.copy(errorStream, System.out);
        waitList.add(p);
    }

    void verifyServerCa(TcpHost caHost, String fingerprint) throws Exception {
        downloadCertificate(caHost, "ca", CA_FILE_PATH);
        downloadCertificate(caHost, "chain", INTERMEDIATE_FILE_PATH);

        String fprFromCa = getFprFromFile();
        if (! fprFromCa.equals(fingerprint)){
            throw new SecurityException(String.format("fingerprints do not match!\nLocal: %s\nOther: %s",
                    fingerprint,
                    fprFromCa));
        }

        importCertificate("ca");
        importCertificate("intermediate");
    }

    void handleTicket(String ticket, int id) throws InitialisationException {
        try {
            SentryManager manager = new SentryManager(HttpClientFactory.getClient(),
                    ticketHost);
            String pemFile = readFromFile(CSR_FILE_PATH);
            Ticket request = new Ticket(id, ticket, pemFile);
            String certificate = manager.requestCertificate(request);
            writeToFile(certificate, CERT_FILE_PATH);
            importCertificate("client");
        } catch (PrintableException e) {
            throw new InitialisationException(e.getMessage(), e);
        } catch (IOException e) {
            throw new InitialisationException("There is a problem with the files", e);
        }
    }

    void waitFor() throws InterruptedException {
        for (Process p : waitList) {
            p.waitFor();
        }
        waitList.clear();
    }

    private String getFprFromFile() throws Exception {
        String command = "openssl x509 " +
                "-noout " +
                "-text " +
                "-fingerprint " +
                "-sha256 " +
                "-in " + CA_FILE_PATH;
        Process p = Runtime.getRuntime().exec(command);
        p.waitFor();
        String output = IOUtils.toString(p.getInputStream());

        Pattern pattern = Pattern.compile("SHA256 Fingerprint=.*");
        Matcher match = pattern.matcher(output);
        String result;
        if (match.find()){
            result = match.group(0);
            result = result.substring(result.indexOf('=')+1, result.length());
        } else {
            throw new IOException("Failed to parse fingerprint");
        }

        return result;
    }

    private void importCertificate(String file) throws IOException {
        String command = String.format("keytool -importcert " +
                        "-noprompt " +
                        "-alias %s " +
                        "-file %s " +
                        "-keypass %s " +
                        "-keystore %s " +
                        "-storepass %s ",
                file,
                Configuration.STOCKS_HOME + "/" + file + ".cert.pem",
                Configuration.KEYSTORE_PASSWORD,
                Configuration.KEYSTORE_PATH,
                Configuration.KEYSTORE_PASSWORD);
        Process p = Runtime.getRuntime().exec(command);
        IOUtils.copy(p.getInputStream(), System.out);
        IOUtils.copy(p.getErrorStream(), System.out);
        try {
            p.waitFor(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
        }
    }

    private void downloadCertificate (TcpHost caHost, String urlResource, String destPath) throws Exception {
        URL website = new URL(String.format("http://%s/%s",
                caHost.toString(), urlResource));
        ReadableByteChannel rbc = Channels.newChannel(website.openStream());
        FileOutputStream fos = new FileOutputStream(destPath);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        rbc.close();
        fos.close();
    }

    private String readFromFile(String path) throws IOException {
        FileInputStream source = new FileInputStream(path);
        String result = IOUtils.toString(source);
        source.close();
        return result;
    }

    private void writeToFile(String content, String path) throws IOException {
        FileOutputStream output = new FileOutputStream(path);
        IOUtils.write(content.getBytes(), output);
        output.close();
    }

}

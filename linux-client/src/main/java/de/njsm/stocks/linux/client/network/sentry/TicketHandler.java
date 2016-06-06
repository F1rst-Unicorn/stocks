package de.njsm.stocks.linux.client.network.sentry;

import de.njsm.stocks.linux.client.Configuration;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketHandler {

    protected final Configuration c;

    public static final String caFilePath           = Configuration.stocksHome + "/ca.cert.pem";
    public static final String intermediateFilePath = Configuration.stocksHome + "/intermediate.cert.pem";
    public static final String csrFilePath          = Configuration.stocksHome + "/client.csr.pem";
    public static final String certFilePath         = Configuration.stocksHome + "/client.cert.pem";

    public TicketHandler (Configuration c) {
        this.c = c;
    }

    public void handleTicket(String ticket, int id) {
        try {
            SentryManager manager = new SentryManager(c);
            manager.requestCertificate(ticket, id);
            importCertificate("client");

        } catch (Exception e){
            c.getLog().log(Level.SEVERE, "TicketHandler: Certificate receiving failed: " + e.getMessage());
            new File(Configuration.keystorePath).delete();
        }
    }

    public void verifyServerCa(String fingerprint) {
        try {
            URL website = new URL(String.format("http://%s:%d/ca",
                    c.getServerName(),
                    c.getCaPort()));
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(caFilePath);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            website = new URL(String.format("http://%s:%d/chain",
                    c.getServerName(),
                    c.getCaPort()));
            rbc = Channels.newChannel(website.openStream());
            fos = new FileOutputStream(intermediateFilePath);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            String fprFromCa = getFprFromFile();
            if (! fprFromCa.equals(fingerprint)){
                throw new SecurityException(String.format("fingerprints do not match!\nLocal: %s\nOther: %s",
                        fingerprint,
                        fprFromCa));
            }

            importCertificate("ca");
            importCertificate("intermediate");

        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "TicketHandler: Unable to get CA certificate: " + e.getMessage());
        }
    }

    public void generateKey(String username, String deviceName, int[] ids) throws Exception {

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
                Configuration.keystorePassword,
                Configuration.keystorePath,
                Configuration.keystorePassword);
        Process p = Runtime.getRuntime().exec(keyGenCommand);
        InputStream resultStream = p.getInputStream();
        InputStream errorStream = p.getErrorStream();
        IOUtils.copy(resultStream, System.out);
        IOUtils.copy(errorStream, System.out);
        p.waitFor();
    }

    public void generateCsr() throws Exception {
        String getCsrCommand = String.format("keytool -certreq " +
                        "-alias client " +
                        "-file %s " +
                        "-keypass %s " +
                        "-keystore %s " +
                        "-storepass %s ",
                csrFilePath,
                Configuration.keystorePassword,
                Configuration.keystorePath,
                Configuration.keystorePassword);
        Process p = Runtime.getRuntime().exec(getCsrCommand);
        InputStream resultStream = p.getInputStream();
        InputStream errorStream = p.getErrorStream();
        IOUtils.copy(resultStream, System.out);
        IOUtils.copy(errorStream, System.out);
        p.waitFor();
    }

    protected String getFprFromFile() throws Exception {
        String command = "openssl x509 " +
                "-noout " +
                "-text " +
                "-fingerprint " +
                "-sha256 " +
                "-in " + caFilePath;
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

    protected void importCertificate(String file) throws Exception {
        String command = String.format("keytool -importcert " +
                        "-noprompt " +
                        "-alias %s " +
                        "-file %s " +
                        "-keypass %s " +
                        "-keystore %s " +
                        "-storepass %s ",
                file,
                Configuration.stocksHome + "/" + file + ".cert.pem",
                Configuration.keystorePassword,
                Configuration.keystorePath,
                Configuration.keystorePassword);
        Process p = Runtime.getRuntime().exec(command);
        IOUtils.copy(p.getInputStream(), System.out);
        IOUtils.copy(p.getErrorStream(), System.out);
        p.waitFor();
    }

}

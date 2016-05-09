package de.njsm.stocks.linux.client.network;

import de.njsm.stocks.linux.client.CertificateManager;
import de.njsm.stocks.linux.client.Configuration;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.concurrent.SynchronousQueue;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketHandler {

    protected Configuration c;

    public TicketHandler (Configuration c) {
        this.c = c;
    }

    public void handleTicket(String ticket, int id) {
        try {
            SentryManager manager = new SentryManager(c);
            manager.requestCertificate(ticket, id);
            importCertificate("client");

        } catch (Exception e){
            c.getLog().log(Level.SEVERE, "TicketHandler: Certificate retrival failed: " + e.getMessage());
        }
    }

    public void verifyServerCa(String fingerprint) {
        String caFile = Configuration.stocksHome + "/ca.cert.pem";
        String chainFile = Configuration.stocksHome + "/intermediate.cert.pem";
        try {
            URL website = new URL(String.format("http://%s:%d/ca",
                    c.getServerName(),
                    c.getCaPort()));
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(caFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            website = new URL(String.format("http://%s:%d/chain",
                    c.getServerName(),
                    c.getCaPort()));
            rbc = Channels.newChannel(website.openStream());
            fos = new FileOutputStream(chainFile);
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

    protected String getFprFromFile() throws Exception {
        String command = "openssl x509 " +
                "-noout " +
                "-text " +
                "-fingerprint " +
                "-sha256 " +
                "-in " + Configuration.stocksHome + "/ca.cert.pem";
        Process p = Runtime.getRuntime().exec(command);
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
                CertificateManager.keystorePassword,
                CertificateManager.keystorePath,
                CertificateManager.keystorePassword);
        Runtime.getRuntime().exec(command);
    }

}

package de.njsm.stocks.linux.client.network;

import de.njsm.stocks.linux.client.CertificateManager;
import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.frontend.CertificateGenerator;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TicketHandler {

    protected Configuration c;

    public TicketHandler (Configuration c) {
        this.c = c;
    }

    public void handleTicket(String ticket) {
        try {
            // send ticket to server


            // store signed certificate

        } catch (Exception e){
            c.getLog().log(Level.SEVERE, "TicketHandler: Certificate retrival failed: " + e.getMessage());
        }
    }

    public void verifyServerCa(String fingerprint) {
        String caFile = Configuration.stocksHome + "/ca.cert.pem";
        try {
            URL website = new URL(String.format("http://%s:%d/ca",
                    c.getServerName(),
                    c.getCaPort()));
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(caFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            String fprFromCa = getFprFromFile();
            if (! fprFromCa.equals(fingerprint)){
                throw new SecurityException("fingerprints do not match!");
            }

            String importCaKeyCommand = String.format("keytool -importcert " +
                    "-alias CA " +
                    "-file %s " +
                    "-keypass %s" +
                    "-keystore %s" +
                    "-storepass %s",
                    caFile,
                    CertificateManager.keystorePassword,
                    CertificateManager.keystorePath,
                    CertificateManager.keystorePassword);
            Runtime.getRuntime().exec(importCaKeyCommand);

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
        IOUtils.copy(p.getErrorStream(), System.out);
        System.out.println(output);

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

}

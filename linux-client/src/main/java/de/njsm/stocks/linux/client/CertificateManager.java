package de.njsm.stocks.linux.client;

import de.njsm.stocks.linux.client.frontend.UIFactory;

import java.io.File;

public class CertificateManager {

    public static final String keystorePath = System.getProperty("user.home") + "/.stocks/keystore";
    public static final String keystorePassword = System.getProperty("de.njsm.stocks.client.cert.password",
            "thisisapassword");

    protected Configuration c;

    public CertificateManager(Configuration c){
        this.c = c;
    }

    public void loadCertificates(UIFactory f) {

        if (! hasCerts()) {
            (new InitManager(c)).initCertificates(f.getCertGenerator());
        }

    }

    public boolean hasCerts() {
        return new File(keystorePath).exists();
    }
}

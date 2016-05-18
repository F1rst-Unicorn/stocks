package de.njsm.stocks.linux.client.network.server;

import com.squareup.okhttp.OkHttpClient;
import de.njsm.stocks.linux.client.CertificateManager;
import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Update;
import de.njsm.stocks.linux.client.network.sentry.SentryClient;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Level;

public class ServerManager {

    protected ServerClient backend;

    public ServerManager(Configuration c) {
        try {
            String url = String.format("https://%s:%d/",
                    c.getServerName(),
                    c.getServerPort());

            backend = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(getClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(ServerClient.class);
        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "Failed to set up ServerManager: " + e.getMessage());
        }
    }

    public Update[] getUpdates() {
        return backend.getUpdates();
    }

    protected OkHttpClient getClient() throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory
                .getInstance(TrustManagerFactory.getDefaultAlgorithm());
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(new FileInputStream(CertificateManager.keystorePath),
                CertificateManager.keystorePassword.toCharArray());
        tmf.init(ks);

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, CertificateManager.keystorePassword.toCharArray());

        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(),
                tmf.getTrustManagers(),
                new SecureRandom());

        return new OkHttpClient()
                .setSslSocketFactory(context.getSocketFactory())
                .setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String s, SSLSession sslSession) {
                        return true;
                    }
                });
    }
}

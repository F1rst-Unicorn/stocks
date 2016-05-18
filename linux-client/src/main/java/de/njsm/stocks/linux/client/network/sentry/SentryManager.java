package de.njsm.stocks.linux.client.network.sentry;

import com.squareup.okhttp.*;
import de.njsm.stocks.linux.client.CertificateManager;
import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Ticket;
import org.apache.commons.io.IOUtils;
import retrofit.*;
import retrofit.Call;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.logging.Level;

public class SentryManager {

    protected SentryClient backend;

    public SentryManager(Configuration c) {
        try {
            String url = String.format("https://%s:%d/",
                    c.getServerName(),
                    c.getTicketPort());

            backend = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(getClient())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build()
                    .create(SentryClient.class);
        } catch (Exception e) {
            c.getLog().log(Level.SEVERE, "Failed to set up SentryManager: " + e.getMessage());
        }

    }

    public void requestCertificate(String ticket, int id) throws Exception {

        // set up file
        File csr = new File(Configuration.stocksHome + "/client.csr.pem");
        Ticket request = new Ticket();
        request.deviceId = id;
        request.ticket = ticket;
        request.pemFile = IOUtils.toString(new FileInputStream(csr));

        // execute call
        Call<Ticket> callback = backend.requestCertificate(request);
        retrofit.Response<Ticket> response = callback.execute();

        if (response.isSuccess()) {
            Ticket responseTicket = response.body();
            // store result
            if (responseTicket.pemFile == null) {
                throw new SecurityException("Server rejected ticket!");
            }
            FileOutputStream output = new FileOutputStream(Configuration.stocksHome + "/client.cert.pem");
            IOUtils.write(responseTicket.pemFile.getBytes(), output);
            output.close();
        } else {
            throw new Exception("Retrofit call failed: " + response.raw().toString());
        }


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

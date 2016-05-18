package de.njsm.stocks.linux.client.network.sentry;

import de.njsm.stocks.linux.client.Configuration;
import de.njsm.stocks.linux.client.data.Ticket;
import org.apache.commons.io.IOUtils;
import retrofit.*;
import retrofit.Call;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
                    .client(c.getClient())
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
            FileOutputStream concatStream = new FileOutputStream(Configuration.stocksHome + "/client.chain.cert.pem");
            IOUtils.copy(new FileInputStream(Configuration.stocksHome + "/client.cert.pem"), concatStream);
            IOUtils.copy(new FileInputStream(Configuration.stocksHome + "/intermediate.cert.pem"), concatStream);
            concatStream.close();
            output.close();
        } else {
            throw new Exception("Retrofit call failed: " + response.raw().toString());
        }


    }

}

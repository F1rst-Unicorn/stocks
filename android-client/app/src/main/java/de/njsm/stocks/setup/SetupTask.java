package de.njsm.stocks.setup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.jce.provider.PEMUtil;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

import javax.security.auth.x500.X500Principal;

import de.njsm.stocks.Config;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.data.Ticket;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetupTask extends AsyncTask<Void, String, Result> {

    protected Context c;
    protected Config config;

    protected ProgressDialog dialog;

    protected KeyPair clientKeys;
    protected String csr;
    protected String clientCert;
    protected String caCert;
    protected String intermediateCert;

    public SetupTask(Context c) {
        super();
        dialog = new ProgressDialog(c);
        this.c = c;
        config = new Config(c);
    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle("Registering");
        dialog.setProgress(0);
        dialog.setMax(5);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Result doInBackground(Void... params) {

        if (android.os.Debug.isDebuggerConnected()) {
            android.os.Debug.waitForDebugger();
        }
        Result result;
        try {
            publishProgress("Get server certificate");
            downloadCa();

            publishProgress("Check server certificate");
            verifyCa();

            publishProgress("Create key");
            generateKey();

            publishProgress("Registering key");
            registerKey();

            result = Result.SUCCESS;
        } catch (Exception e) {
            result = new Result("Error", e.getMessage(), false);
        }

        return result;
    }

    private void downloadCa() throws Exception {
        String baseUrl = String.format(Locale.US, "http://%s:%d/",
                config.getServerName(),
                config.getCaPort());
        URL website = new URL(baseUrl + "ca");
        caCert = IOUtils.toString(website.openStream());

        website = new URL(baseUrl + "chain");
        intermediateCert = IOUtils.toString(website.openStream());
    }

    private void verifyCa() throws Exception {
        Certificate cert = getCertificate(caCert);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(cert.getEncoded());
        byte[] digest = md.digest();

        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7',
                '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        StringBuffer buf = new StringBuffer(digest.length * 2);
        for (byte aDigest : digest) {
            buf.append(hexDigits[(aDigest & 0xf0) >> 4]);
            buf.append(hexDigits[aDigest & 0x0f]);
            buf.append(":");
        }
        buf.delete(buf.length()-1, buf.length());

        String actualFpr = buf.toString();
        String expectedFpr = config.getFpr();

        if (! expectedFpr.equals(actualFpr)) {
            throw new SecurityException("Wrong fingerprint");
        }
    }

    private void generateKey() throws Exception {
        int keysize = 4096;
        String keyAlgName = "RSA";
        String sigAlgName = "SHA256WithRSA";

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null);

        KeyPairGenerator gen = KeyPairGenerator.getInstance(keyAlgName);
        gen.initialize(keysize);
        clientKeys = gen.generateKeyPair();

        X500Principal principals = new X500Principal("CN=" +
                config.getUsername() + "$" +
                config.getUid() + "$" +
                config.getDeviceName() + "$" +
                config.getDid());
        PKCS10CertificationRequest request =
                new PKCS10CertificationRequest(sigAlgName,
                        principals,
                        clientKeys.getPublic(),
                        null,
                        clientKeys.getPrivate());
        StringBuilder buf = new StringBuilder();
        PEMWriter writer = new PEMWriter(new StringBuilderWriter(buf));
        writer.writeObject(request);
        writer.flush();
        writer.close();
        csr = buf.toString();

        keystore.setCertificateEntry("ca", getCertificate(caCert));
        keystore.setCertificateEntry("intermediate", getCertificate(intermediateCert));
        keystore.store(c.openFileOutput("keystore", Context.MODE_PRIVATE), config.getPassword().toCharArray());
    }

    private void registerKey() throws Exception {
        String url = String.format(Locale.US, "https://%s:%d/",
                config.getServerName(),
                config.getSentryPort());

        Gson gson = new GsonBuilder().create();

        SentryClient backend = new Retrofit.Builder()
                .baseUrl(url)
                .client(config.getClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(SentryClient.class);

        Ticket request = new Ticket();
        request.deviceId = config.getDid();
        request.ticket = config.getTicket();
        request.pemFile = csr;
        Call<Ticket> callback = backend.requestCertificate(request);
        retrofit2.Response<Ticket> response = callback.execute();

        if (response.isSuccessful()) {
            Ticket responseTicket = response.body();
            // store result
            if (responseTicket.pemFile == null) {
                throw new SecurityException("Server rejected ticket!");
            }
            clientCert = responseTicket.pemFile;
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(this.c.openFileInput("keystore"), config.getPassword().toCharArray());

            Certificate[] trustChain = new Certificate[3];
            trustChain[0] = getCertificate(clientCert);
            trustChain[1] = getCertificate(intermediateCert);
            trustChain[2] = getCertificate(caCert);

            keystore.setKeyEntry("client", clientKeys.getPrivate().getEncoded(), trustChain);
        } else {
            throw new Exception("Invalid server answer: " + response.raw().toString());
        }

    }

    private Certificate getCertificate(String pemString) throws Exception {
        PEMReader reader = new PEMReader(new InputStreamReader(IOUtils.toInputStream(pemString)));
        Object rawCert = reader.readObject();
        if (rawCert instanceof Certificate) {
            return (Certificate) rawCert;
        } else {
            throw new Exception("Certificate is unreadable");
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        dialog.setMessage(values[0]);
        dialog.setProgress(dialog.getProgress()+1);
    }

    @Override
    protected void onPostExecute(Result s) {
        dialog.cancel();

        AlertDialog.Builder builder = new AlertDialog.Builder(c)
                .setTitle(s.getTitle())
                .setMessage(s.getMessage())
                .setCancelable(false);

        if (s.isSuccess()) {
            builder.setIcon(R.drawable.ic_check_black_24dp)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(c, SetupActivity.class);
                    c.startActivity(i);
                }
            }).setNegativeButton("ABORT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).setIcon(R.drawable.ic_error_black_24dp);
        }

        AlertDialog messageDialog = builder.create();
        messageDialog.show();
    }
}

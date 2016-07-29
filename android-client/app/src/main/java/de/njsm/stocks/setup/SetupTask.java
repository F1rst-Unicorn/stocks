package de.njsm.stocks.setup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.Locale;

import javax.security.auth.x500.X500Principal;

import de.njsm.stocks.Config;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.data.Ticket;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SetupTask extends AsyncTask<Void, String, Result> {

    protected Activity c;
    protected Bundle extras;
    protected SetupFinishedListener mListener;

    protected ProgressDialog dialog;

    protected KeyPair clientKeys;
    protected String csr;
    protected String clientCert;
    protected String caCert;
    protected String intermediateCert;

    public SetupTask(Activity c) {
        super();
        dialog = new ProgressDialog(c);
        this.c = c;
        extras = c.getIntent().getExtras();
    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle(c.getResources().getString(R.string.dialog_registering));
        dialog.setProgress(0);
        dialog.setMax(5);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Result doInBackground(Void... params) {

        Result result;

        File keystore = new File(c.getFilesDir().getAbsolutePath()+"/keystore");
        if (keystore.exists()){
            result = Result.getSuccess(c);

        } else try {
            publishProgress(c.getResources().getString(R.string.dialog_get_cert));
            downloadCa();

            publishProgress(c.getResources().getString(R.string.dialog_verify_cert));
            verifyCa();

            publishProgress(c.getResources().getString(R.string.dialog_create_key));
            generateKey();

            publishProgress(c.getResources().getString(R.string.dialog_register_key));
            registerKey();

            publishProgress(c.getResources().getString(R.string.dialog_store_settings));
            storeConfig();

            result = Result.getSuccess(c);
        } catch (Exception e) {
            result = new Result(c.getResources().getString(R.string.dialog_error),
                    e.getMessage(),
                    false);
            keystore.delete();
        }

        return result;
    }

    private void downloadCa() throws Exception {
        String baseUrl = String.format(Locale.US, "http://%s:%d/",
                extras.getString(Config.serverNameConfig),
                extras.getInt(Config.caPortConfig));
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
        StringBuilder buf = new StringBuilder(digest.length * 2);
        for (byte aDigest : digest) {
            buf.append(hexDigits[(aDigest & 0xf0) >> 4]);
            buf.append(hexDigits[aDigest & 0x0f]);
            buf.append(":");
        }
        buf.delete(buf.length()-1, buf.length());

        String actualFpr = buf.toString();
        String expectedFpr = extras.getString(Config.fprConfig, "");

        if (! expectedFpr.equals(actualFpr)) {
            throw new SecurityException(c.getResources().getString(R.string.dialog_wrong_fpr));
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
                extras.getString(Config.usernameConfig) + "$" +
                extras.getInt(Config.uidConfig) + "$" +
                extras.getString(Config.deviceNameConfig) + "$" +
                extras.getInt(Config.didConfig));
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
        FileOutputStream out = c.openFileOutput("keystore", Context.MODE_PRIVATE);
        keystore.store(out, Config.password.toCharArray());
        out.flush();
        out.close();
    }

    private void registerKey() throws Exception {
        String url = String.format(Locale.US, "https://%s:%d/",
                extras.getString(Config.serverNameConfig),
                extras.getInt(Config.sentryPortConfig));

        Gson gson = new GsonBuilder().create();

        SentryClient backend = new Retrofit.Builder()
                .baseUrl(url)
                .client(Config.getClient(c))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(SentryClient.class);

        Ticket request = new Ticket();
        request.deviceId = extras.getInt(Config.didConfig);
        request.ticket = extras.getString(Config.ticketConfig);
        request.pemFile = csr;
        Call<Ticket> callback = backend.requestCertificate(request);
        retrofit2.Response<Ticket> response = callback.execute();

        if (response.isSuccessful()) {
            Ticket responseTicket = response.body();
            // store result
            if (responseTicket.pemFile == null) {
                throw new SecurityException(c.getResources().getString(R.string.dialog_ticket_reject));
            }
            clientCert = responseTicket.pemFile;
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(this.c.openFileInput("keystore"), Config.password.toCharArray());

            Certificate[] trustChain = new Certificate[3];
            trustChain[0] = getCertificate(clientCert);
            trustChain[1] = getCertificate(intermediateCert);
            trustChain[2] = getCertificate(caCert);

            keystore.setKeyEntry("client", clientKeys.getPrivate(), Config.password.toCharArray(), trustChain);
            FileOutputStream out = c.openFileOutput("keystore", Context.MODE_PRIVATE);
            keystore.store(out, Config.password.toCharArray());
            out.flush();
            out.close();
        } else {
            throw new Exception(c.getResources().getString(R.string.dialog_invalid_answer) + response.raw().toString());
        }
    }

    private void storeConfig() {
        SharedPreferences prefs = c.getSharedPreferences(Config.preferences, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(Config.serverNameConfig, extras.getString(Config.serverNameConfig))
                .putInt(Config.caPortConfig, extras.getInt(Config.caPortConfig))
                .putInt(Config.sentryPortConfig, extras.getInt(Config.sentryPortConfig))
                .putInt(Config.serverPortConfig, extras.getInt(Config.serverPortConfig))
                .putString(Config.usernameConfig, extras.getString(Config.usernameConfig))
                .putString(Config.deviceNameConfig, extras.getString(Config.deviceNameConfig))
                .putInt(Config.uidConfig, extras.getInt(Config.uidConfig))
                .putInt(Config.didConfig, extras.getInt(Config.didConfig))
                .putString(Config.fprConfig, extras.getString(Config.fprConfig))
                .putString(Config.ticketConfig, extras.getString(Config.ticketConfig))
                .commit();
    }

    private Certificate getCertificate(String pemString) throws Exception {
        PEMReader reader = new PEMReader(new InputStreamReader(IOUtils.toInputStream(pemString)));
        Object rawCert = reader.readObject();
        if (rawCert instanceof Certificate) {
            return (Certificate) rawCert;
        } else {
            throw new Exception(c.getResources().getString(R.string.dialog_cert_unreadable));
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        dialog.setMessage(values[0]);
        dialog.setProgress(dialog.getProgress()+1);
    }

    public void registerListener(SetupFinishedListener l) {
        mListener = l;
    }

    @Override
    protected void onPostExecute(Result s) {
        dialog.cancel();

        AlertDialog.Builder builder = new AlertDialog.Builder(c)
                .setTitle(s.getTitle())
                .setMessage(s.getMessage())
                .setCancelable(false);

        if (s.isSuccess()) {
            mListener.finished();
            builder.setIcon(R.drawable.ic_check_black_24dp)
                    .setPositiveButton(c.getResources().getString(R.string.dialog_ok),
                            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        } else {
            builder.setPositiveButton(c.getResources().getString(R.string.dialog_retry),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent i = new Intent(c, SetupActivity.class);
                    i.putExtras(extras);
                    c.startActivity(i);
                }
            }).setNegativeButton(c.getResources().getString(R.string.dialog_abort),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    c.finish();
                }
            }).setIcon(R.drawable.ic_error_black_24dp);
        }

        AlertDialog messageDialog = builder.create();
        messageDialog.show();
    }
}

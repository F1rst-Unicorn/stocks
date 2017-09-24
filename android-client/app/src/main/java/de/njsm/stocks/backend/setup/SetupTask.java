package de.njsm.stocks.backend.setup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.sentry.SentryClient;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.backend.util.Config;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.error.TextResourceException;
import de.njsm.stocks.frontend.setup.Result;
import de.njsm.stocks.frontend.setup.SetupActivity;
import de.njsm.stocks.frontend.setup.SetupFinishedListener;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PEMWriter;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.security.auth.x500.X500Principal;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.util.Locale;

import static de.njsm.stocks.backend.util.Config.KEYSTORE_FILE;

public class SetupTask extends AbstractAsyncTask<Void, String, Result> {

    private Activity c;
    private Bundle extras;
    private SetupFinishedListener mListener;

    private ProgressDialog dialog;

    private KeyPair clientKeys;
    private String csr;
    private String caCert;
    private String intermediateCert;

    public SetupTask(Activity c) {
        super(c.getFilesDir());
        dialog = new ProgressDialog(c);
        this.c = c;
        extras = c.getIntent().getExtras();
    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle(c.getResources().getString(R.string.dialog_registering));
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    @Override
    protected Result doInBackgroundInternally(Void... params) {

        Result result;

        File keystore = new File(c.getFilesDir().getAbsolutePath()+"/keystore");
        if (keystore.exists()){
            result = Result.getSuccess();

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

            result = Result.getSuccess();
        } catch (TextResourceException e) {
            Log.e(Config.LOG_TAG, "Caught exception during initialisation", e);
            result = new Result(R.string.dialog_error,
                    e.getResourceId(),
                    false);
            keystore.delete();
        } catch (Exception e) {
            Log.e(Config.LOG_TAG, "Caught exception during initialisation", e);
            result = new Result(R.string.dialog_error,
                    R.string.dialog_invalid_answer,
                    false);
            keystore.delete();
        }

        return result;
    }

    private void downloadCa() throws Exception {
        String baseUrl = String.format(Locale.US, "http://%s:%d/",
                extras.getString(Config.SERVER_NAME_CONFIG),
                extras.getInt(Config.CA_PORT_CONFIG));
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
        String expectedFpr = extras.getString(Config.FPR_CONFIG, "");

        if (! expectedFpr.equals(actualFpr)) {
            throw new TextResourceException(R.string.dialog_wrong_fpr);
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
                extras.getString(Config.USERNAME_CONFIG) + "$" +
                extras.getInt(Config.UID_CONFIG) + "$" +
                extras.getString(Config.DEVICE_NAME_CONFIG) + "$" +
                extras.getInt(Config.DID_CONFIG));
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
        keystore.store(out, Config.PASSWORD.toCharArray());
        out.flush();
        out.close();
    }

    private void registerKey() throws Exception {
        String url = String.format(Locale.US, "https://%s:%d/",
                extras.getString(Config.SERVER_NAME_CONFIG),
                extras.getInt(Config.SENTRY_PORT_CONFIG));

        SentryClient backend = new Retrofit.Builder()
                .baseUrl(url)
                .client(Config.getClient(c.openFileInput(KEYSTORE_FILE)))
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SentryClient.class);

        Ticket request = new Ticket(extras.getInt(Config.DID_CONFIG),
                extras.getString(Config.TICKET_CONFIG),
                csr);
        Call<Ticket> callback = backend.requestCertificate(request);
        retrofit2.Response<Ticket> response = callback.execute();

        if (response.isSuccessful()) {
            Ticket responseTicket = response.body();

            if (responseTicket.pemFile == null) {
                throw new TextResourceException(R.string.dialog_ticket_reject);
            }
            String clientCert = responseTicket.pemFile;
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(this.c.openFileInput("keystore"), Config.PASSWORD.toCharArray());

            Certificate[] trustChain = new Certificate[3];
            trustChain[0] = getCertificate(clientCert);
            trustChain[1] = getCertificate(intermediateCert);
            trustChain[2] = getCertificate(caCert);

            keystore.setKeyEntry("client", clientKeys.getPrivate(), Config.PASSWORD.toCharArray(), trustChain);
            FileOutputStream out = c.openFileOutput("keystore", Context.MODE_PRIVATE);
            keystore.store(out, Config.PASSWORD.toCharArray());
            out.flush();
            out.close();
        } else {
            throw new Exception(c.getResources().getString(R.string.dialog_invalid_answer) + response.raw().toString());
        }
    }

    private void storeConfig() {
        SharedPreferences prefs = c.getSharedPreferences(Config.PREFERENCES_FILE, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(Config.SERVER_NAME_CONFIG, extras.getString(Config.SERVER_NAME_CONFIG))
                .putInt(Config.CA_PORT_CONFIG, extras.getInt(Config.CA_PORT_CONFIG))
                .putInt(Config.SENTRY_PORT_CONFIG, extras.getInt(Config.SENTRY_PORT_CONFIG))
                .putInt(Config.SERVER_PORT_CONFIG, extras.getInt(Config.SERVER_PORT_CONFIG))
                .putString(Config.USERNAME_CONFIG, extras.getString(Config.USERNAME_CONFIG))
                .putString(Config.DEVICE_NAME_CONFIG, extras.getString(Config.DEVICE_NAME_CONFIG))
                .putInt(Config.UID_CONFIG, extras.getInt(Config.UID_CONFIG))
                .putInt(Config.DID_CONFIG, extras.getInt(Config.DID_CONFIG))
                .putString(Config.FPR_CONFIG, extras.getString(Config.FPR_CONFIG))
                .putString(Config.TICKET_CONFIG, extras.getString(Config.TICKET_CONFIG))
                .commit();
    }

    private Certificate getCertificate(String pemString) throws IOException, TextResourceException {
        PEMReader reader = new PEMReader(new InputStreamReader(IOUtils.toInputStream(pemString)));
        Object rawCert = reader.readObject();
        if (rawCert instanceof Certificate) {
            return (Certificate) rawCert;
        } else {
            throw new TextResourceException(R.string.dialog_cert_unreadable);
        }
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        dialog.setMessage(values[0]);
    }

    public void registerListener(SetupFinishedListener l) {
        mListener = l;
    }

    @Override
    protected void onPostExecute(Result result) {
        AlertDialog.Builder builder = new AlertDialog.Builder(c)
                .setTitle(result.getTitle())
                .setMessage(result.getMessage())
                .setCancelable(false);

        if (result.isSuccess()) {
            builder.setIcon(R.drawable.ic_check_black_24dp)
                    .setPositiveButton(c.getResources().getString(R.string.dialog_ok), (dialog, which) -> {
                        dialog.dismiss();
                        mListener.onSetupFinished();
                    });
        } else {
            builder.setPositiveButton(c.getResources().getString(R.string.dialog_retry), (dialog, which) -> {
                    dialog.dismiss();
                    Intent i = new Intent(c, SetupActivity.class);
                    i.putExtras(extras);
                    c.startActivity(i);
            }).setNegativeButton(c.getResources().getString(R.string.dialog_abort),(dialog, which) -> {
                    dialog.dismiss();
                    c.finish();
            }).setIcon(R.drawable.ic_error_black_24dp);
        }

        AlertDialog messageDialog = builder.create();
        dialog.dismiss();
        messageDialog.show();
    }
}
package de.njsm.stocks.backend.setup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import de.njsm.stocks.Config;
import de.njsm.stocks.R;
import de.njsm.stocks.backend.network.sentry.SentryClient;
import de.njsm.stocks.backend.util.AbstractAsyncTask;
import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.error.TextResourceException;
import de.njsm.stocks.frontend.setup.Result;
import de.njsm.stocks.frontend.setup.SetupActivity;
import de.njsm.stocks.frontend.setup.SetupFinishedListener;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.openssl.PEMReader;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.util.Locale;

import static de.njsm.stocks.Config.KEYSTORE_FILE;

public class SetupTask extends AbstractAsyncTask<Void, String, Result> {

    private Activity c;
    
    private Bundle extras;
    
    private SetupFinishedListener mListener;

    private ProgressDialog dialog;

    private KeyPair clientKeys;

    private String csr;

    private String caCert;

    private String intermediateCert;

    private KeystoreHandler keystoreHandler;

    public SetupTask(Activity c) {
        super(c);
        dialog = new ProgressDialog(c);
        this.c = c;
        extras = c.getIntent().getExtras();
        keystoreHandler = new KeystoreHandler();
    }

    @Override
    protected void onPreExecute() {
        Log.i(Config.LOG_TAG, "Starting registration");
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
            Log.w(Config.LOG_TAG, "Keystore found, app is already initialised");
            result = Result.getSuccess();

        } else try {
            Log.i(Config.LOG_TAG, "Starting initialisation");

            publishProgress(c.getResources().getString(R.string.dialog_get_cert));
            downloadCa();

            publishProgress(c.getResources().getString(R.string.dialog_verify_cert));
            keystoreHandler.verifyCaCertificate(caCert, extras.getString(Config.FPR_CONFIG, ""));

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

        Log.i(Config.LOG_TAG, "Downloading CA certificate");

        URL website = new URL(baseUrl + "ca");
        caCert = IOUtils.toString(website.openStream());

        Log.i(Config.LOG_TAG, "Downloading certificate chain");

        website = new URL(baseUrl + "chain");
        intermediateCert = IOUtils.toString(website.openStream());
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null);

        keystore.setCertificateEntry("ca", getCertificate(caCert));
        keystore.setCertificateEntry("intermediate", getCertificate(intermediateCert));
        FileOutputStream out = c.openFileOutput("keystore", Context.MODE_PRIVATE);
        keystore.store(out, Config.PASSWORD.toCharArray());
        out.flush();
        out.close();
    }

    private void generateKey() throws Exception {
        int keySize = 4096;
        String keyAlgName = "RSA";
        String sigAlgName = "SHA256WithRSA";
        keystoreHandler.generateKeys(keyAlgName, keySize);
        keystoreHandler.generateCsr(extras.getString(Config.USERNAME_CONFIG),
                extras.getInt(Config.UID_CONFIG),
                extras.getString(Config.DEVICE_NAME_CONFIG),
                extras.getInt(Config.DID_CONFIG),
                sigAlgName);

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
            Log.e(Config.LOG_TAG, "Invalid answer: " + response.raw().toString());
            throw new TextResourceException(R.string.dialog_invalid_answer);
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
            mListener.finished();
            builder.setIcon(R.drawable.ic_check_black_24dp)
                    .setPositiveButton(c.getResources().getString(R.string.dialog_ok), (dialog, which) ->
                    dialog.dismiss()
            );
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
        dialog.cancel();
        messageDialog.show();
    }
}

package de.njsm.stocks.setup;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.github.fcannizzaro.materialstepper.style.ProgressStepper;

import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.KeyStore;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Locale;

import de.njsm.stocks.Config;
import de.njsm.stocks.R;

public class SetupTask extends AsyncTask<Void, String, Result> {

    ProgressDialog dialog;
    Context c;

    public SetupTask(Context c) {
        super();
        dialog = new ProgressDialog(c);
        this.c = c;

    }

    @Override
    protected void onPreExecute() {
        dialog.setTitle("Registering");
        dialog.setProgress(0);
        dialog.setMax(5);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected Result doInBackground(Void... params) {

        try {
            publishProgress("Get server certificate");
            downloadCa();

            publishProgress("Check server certificate");
            verifyCa();

            // generate key
            publishProgress("Create key");
            generateKey();

            // Talk to sentry
            publishProgress("Registering key");
            Thread.sleep(2000);

        } catch (Exception e) {
            return new Result("Error", e.getMessage(), false);
        }

        return Result.SUCCESS;
    }

    private void downloadCa() throws Exception {
        Config c = new Config(this.c);
        URL website = new URL(String.format(Locale.US, "http://%s:%d/ca",
                c.getPrefs().getString(Config.serverName, null),
                c.getPrefs().getInt(Config.caPort, 0)));
        byte[] data = IOUtils.toByteArray(website.openStream());
        FileOutputStream fos = this.c.openFileOutput("ca.cert.pem", Context.MODE_PRIVATE);
        fos.write(data);
        fos.flush();
        fos.close();

        website = new URL(String.format(Locale.US, "http://%s:%d/chain",
                c.getPrefs().getString(Config.serverName, null),
                c.getPrefs().getInt(Config.caPort, 0)));
        data = IOUtils.toByteArray(website.openStream());
        fos = this.c.openFileOutput("ca-chain.cert.pem", Context.MODE_PRIVATE);
        fos.write(data);
        fos.flush();
        fos.close();
    }

    private void verifyCa() throws Exception {
        FileInputStream is = c.openFileInput("ca.cert.pem");
        CertificateFactory x509CertFact = CertificateFactory.getInstance("X.509");
        X509Certificate cert = (X509Certificate)x509CertFact.generateCertificate(is);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] der = cert.getEncoded();
        md.update(der);
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
        String expectedFpr = new Config(c).getPrefs().getString(Config.fpr, "");

        if (! expectedFpr.equals(actualFpr)) {
            throw new SecurityException("Wrong fingerprint");
        }
    }

    private void generateKey() throws Exception {

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
                .setCancelable(false)
                .setIcon(s.isSuccess() ? R.drawable.ic_check_black_24dp :
                                         R.drawable.ic_error_black_24dp);
        if (s.isSuccess()) {
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
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
            });
        }
        AlertDialog messageDialog = builder.create();
        messageDialog.show();
    }
}

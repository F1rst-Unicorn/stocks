/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.android.service;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.StringBuilderWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Locale;

import javax.security.auth.x500.X500Principal;

import de.njsm.stocks.R;
import de.njsm.stocks.android.dagger.modules.WebModule;
import de.njsm.stocks.android.error.TextResourceException;
import de.njsm.stocks.android.network.sentry.SentryClient;
import de.njsm.stocks.android.network.server.HostnameInterceptor;
import de.njsm.stocks.android.network.server.StatusCode;
import de.njsm.stocks.android.util.Config;
import de.njsm.stocks.android.util.ExceptionHandler;
import de.njsm.stocks.android.util.Logger;
import de.njsm.stocks.android.util.Principals;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class SetupHandler extends Handler {

    public static final String ACTION_UPDATE = "de.njsm.stocks.backend.setup.SetupService.ACTION_UPDATE";

    public static final String ACTION_DONE = "de.njsm.stocks.backend.setup.SetupService.ACTION_DONE";

    public static final String PARAM_TITLE = "de.njsm.stocks.backend.setup.SetupService.PARAM_TITLE";

    public static final String PARAM_MESSAGE = "de.njsm.stocks.backend.setup.SetupService.PARAM_MESSAGE";

    public static final String PARAM_SUCCESS = "de.njsm.stocks.backend.setup.SetupService.PARAM_SUCCESS";

    private static final Logger LOG = new Logger(SetupHandler.class);

    static final int GENERATE_KEY = 0;

    static final int DO_REST_WORK = 1;

    private SetupService c;

    private KeyPair clientKeys;

    private String csr;

    private String caCert;

    private String intermediateCert;

    SetupHandler(Looper looper, SetupService c) {
        super(looper);
        this.c = c;
    }

    @Override
    public void handleMessage(Message msg) {
        if (!(Thread.getDefaultUncaughtExceptionHandler() instanceof ExceptionHandler))
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(c.getFilesDir(),
                    Thread.getDefaultUncaughtExceptionHandler()));
        LOG.d("Received message " + msg.what);

        try {
            switch (msg.what) {
                case GENERATE_KEY:
                    generateKeyPair();
                    break;
                case DO_REST_WORK:
                    Bundle input = (Bundle) msg.obj;
                    downloadCa(input);
                    verifyCa(input);
                    generateCertificate(new Principals(
                            input.getString(Config.USERNAME_CONFIG),
                            input.getString(Config.DEVICE_NAME_CONFIG),
                            input.getInt(Config.UID_CONFIG),
                            input.getInt(Config.DID_CONFIG)));
                    registerKey(input);
                    storeConfig(input);
                    broadcastTermination(R.string.dialog_success,
                            R.string.dialog_finished,
                            true);
            }
        } catch (TextResourceException e) {
            cleanup(e, e.getResourceId());
        } catch (Exception e) {
            cleanup(e, R.string.dialog_invalid_answer);
        }
        LOG.d("Done with message " + msg.what);
    }

    private void cleanup(Exception e, int p) {
        LOG.e("Caught exception during initialisation", e);
        LOG.e("Caught exception during initialisation " + e.getMessage());
        new File(c.getFilesDir().getAbsolutePath() + "/keystore").delete();
        broadcastTermination(R.string.title_error, p, false);
    }

    private void broadcastUpdate(int message) {
        LOG.d("publishing update " + message);
        Intent i = new Intent();
        i.setAction(ACTION_UPDATE);
        i.putExtra(PARAM_MESSAGE, message);
        LocalBroadcastManager.getInstance(c).sendBroadcast(i);
    }

    private void broadcastTermination(int title, int message, boolean success) {
        LOG.d("publishing termination");
        Intent i = new Intent();
        i.setAction(ACTION_DONE);
        i.putExtra(PARAM_TITLE, title);
        i.putExtra(PARAM_MESSAGE, message);
        i.putExtra(PARAM_SUCCESS, success);
        LocalBroadcastManager.getInstance(c).sendBroadcast(i);
    }

    private void generateKeyPair() throws NoSuchAlgorithmException {
        LOG.d("Generating new keypair");
        int keysize = 4096;
        String keyAlgName = "RSA";

        KeyPairGenerator gen = KeyPairGenerator.getInstance(keyAlgName);
        gen.initialize(keysize);
        clientKeys = gen.generateKeyPair();
    }

    private void downloadCa(Bundle extras) throws Exception {
        broadcastUpdate(R.string.dialog_get_cert);
        LOG.d("Getting server certificate");
        String baseUrl = String.format(Locale.US, "http://%s:%d/",
                extras.getString(Config.SERVER_NAME_CONFIG),
                extras.getInt(Config.CA_PORT_CONFIG));
        URL website = new URL(baseUrl + "ca");
        caCert = IOUtils.toString(website.openStream(), StandardCharsets.UTF_8);

        website = new URL(baseUrl + "chain");
        intermediateCert = IOUtils.toString(website.openStream(), StandardCharsets.UTF_8);
    }

    private void verifyCa(Bundle extras) throws Exception {
        broadcastUpdate(R.string.dialog_verify_cert);
        LOG.d("Checking server certificate");
        Certificate cert = convertToCertificate(caCert);
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
        buf.delete(buf.length() - 1, buf.length());

        String actualFpr = buf.toString();
        String expectedFpr = extras.getString(Config.FPR_CONFIG, "");

        if (!expectedFpr.equals(actualFpr)) {
            throw new TextResourceException(R.string.dialog_wrong_fpr);
        }
    }

    private void generateCertificate(Principals user) throws Exception {
        broadcastUpdate(R.string.dialog_create_key);
        LOG.d("Generating CSR");
        String sigAlgName = "SHA256WithRSA";
        X500Principal principals = new X500Principal("CN=" + user);
        PKCS10CertificationRequest request =
                new JcaPKCS10CertificationRequestBuilder(
                        principals,
                        clientKeys.getPublic())
                        .build(new JcaContentSignerBuilder(sigAlgName).build(clientKeys.getPrivate()));

        StringBuilder buf = new StringBuilder();
        JcaPEMWriter writer = new JcaPEMWriter(new StringBuilderWriter(buf));
        writer.writeObject(request);
        writer.flush();
        writer.close();
        csr = buf.toString();

        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null);
        keystore.setCertificateEntry("ca", convertToCertificate(caCert));
        keystore.setCertificateEntry("intermediate", convertToCertificate(intermediateCert));
        FileOutputStream out = c.openFileOutput("keystore", Context.MODE_PRIVATE);
        keystore.store(out, Config.PASSWORD.toCharArray());
        out.flush();
        out.close();
    }

    private void registerKey(Bundle extras) throws Exception {
        broadcastUpdate(R.string.dialog_register_key);
        LOG.d("Registering key");
        String url = String.format(Locale.US, "https://%s:%d/",
                extras.getString(Config.SERVER_NAME_CONFIG),
                extras.getInt(Config.SENTRY_PORT_CONFIG));

        SentryClient backend = new Retrofit.Builder()
                .baseUrl(url)
                .client(WebModule.getClient(c.openFileInput(Config.KEYSTORE_FILE),
                        new HostnameInterceptor(extras.getString(Config.SERVER_NAME_CONFIG),
                                extras.getInt(Config.SENTRY_PORT_CONFIG))))
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(SentryClient.class);

        Call<SentryClient.Result> callback = backend.requestCertificate(
                extras.getInt(Config.DID_CONFIG),
                extras.getString(Config.TICKET_CONFIG),
                csr
        );
        retrofit2.Response<SentryClient.Result> response = callback.execute();

        if (response.isSuccessful()) {
            SentryClient.Result responseTicket = response.body();

            if (responseTicket == null) {
                LOG.e("Got empty response from server");
                throw new TextResourceException(R.string.dialog_invalid_answer);
            } else if (responseTicket.status != StatusCode.SUCCESS) {
                switch (responseTicket.status) {
                    case ACCESS_DENIED:
                        LOG.e("Server rejected ticket");
                        throw new TextResourceException(R.string.dialog_ticket_reject);
                    case GENERAL_ERROR:
                    case DATABASE_UNREACHABLE:
                    case CA_UNREACHABLE:
                        LOG.e("Server had problems issuing certificate: " + response.raw().toString());
                        throw new TextResourceException(R.string.dialog_invalid_answer);
                    default:
                        LOG.e("Got invalid response from server: " + response.raw().toString());
                        throw new TextResourceException(R.string.dialog_invalid_answer);
                }
            }
            String clientCert = responseTicket.data;
            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(this.c.openFileInput("keystore"), Config.PASSWORD.toCharArray());

            Certificate[] trustChain = new Certificate[3];
            trustChain[0] = convertToCertificate(clientCert);
            trustChain[1] = convertToCertificate(intermediateCert);
            trustChain[2] = convertToCertificate(caCert);

            keystore.setKeyEntry("client", clientKeys.getPrivate(), Config.PASSWORD.toCharArray(), trustChain);
            FileOutputStream out = c.openFileOutput("keystore", Context.MODE_PRIVATE);
            keystore.store(out, Config.PASSWORD.toCharArray());
            out.flush();
            out.close();
        } else {
            throw new Exception(c.getResources().getString(R.string.dialog_invalid_answer) + response.raw().toString());
        }
    }

    private void storeConfig(Bundle extras) {
        broadcastUpdate(R.string.dialog_store_settings);
        LOG.d("Saving settings");
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
                .apply();
    }

    private Certificate convertToCertificate(String pemString) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return factory.generateCertificate(IOUtils.toInputStream(pemString, StandardCharsets.UTF_8));
    }

}

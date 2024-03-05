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

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.RestAssured;
import io.restassured.config.LogConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.pkcs.jcajce.JcaPKCS10CertificationRequestBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.security.auth.x500.X500Principal;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static io.restassured.config.SSLConfig.sslConfig;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

public class SetupTest {

    private String subjectName = "Jack$1$Device$1";

    private KeyPair clientKeys;

    private static KeyStore keystore;

    public static final String PASSWORD = "thisisapassword";

    @Before
    public void setup() throws Exception {
        keystore = getFirstKeystore();
        RestAssured.config = RestAssured.config().sslConfig(sslConfig()
                .allowAllHostnames()
                .trustStore(keystore))
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails());
    }

    @After
    public void tearDown() {
        RestAssured.config = RestAssured.config().sslConfig(sslConfig()
                .allowAllHostnames()
                .trustStore(keystore)
                .keyStore("keystore_test", PASSWORD))
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails());

    }

    @Test
    public void setupFirstAccount() throws Exception {
        clientKeys = generateKeyPair();
        String csr = getCsr(clientKeys, subjectName);

        ValidatableResponse response =
        given()
                .log().ifValidationFails()
                .formParam("device", 1)
                .formParam("token", "0000")
                .formParam("csr", csr).
        when().
                post("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/v2/auth/newuser").
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data", not(isEmptyOrNullString()));

        String rawCert = response.extract().jsonPath().getString("data");
        storeToDisk(keystore, rawCert, "keystore_test", clientKeys);

    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        return gen.generateKeyPair();
    }

    public static void storeToDisk(KeyStore keystore, String rawCert, String fileName, KeyPair clientKeys) throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException {
        Certificate clientCert = convertToCertificate(rawCert);
        Certificate[] trustChain = new Certificate[3];
        trustChain[0] = clientCert;
        trustChain[1] = keystore.getCertificate("chain");
        trustChain[2] = keystore.getCertificate("ca");
        keystore.setKeyEntry("client",
                clientKeys.getPrivate(),
                PASSWORD.toCharArray(),
                trustChain);
        keystore.store(new FileOutputStream(fileName), PASSWORD.toCharArray());
    }

    public static KeyStore getFirstKeystore() throws KeyStoreException, CertificateException, IOException, NoSuchAlgorithmException {
        KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
        keystore.load(null);
        String ca = when().
                get("http://" + TestSuite.HOSTNAME + ":" + TestSuite.CA_PORT + "/ca").
        then().extract().body().asString();
        String chain = when().
                get("http://" + TestSuite.HOSTNAME + ":" + TestSuite.CA_PORT + "/chain").
        then().extract().body().asString();
        keystore.setCertificateEntry("ca", convertToCertificate(ca));
        keystore.setCertificateEntry("chain", convertToCertificate(chain));
        return keystore;
    }

    public static String getCsr(KeyPair clientKeys, String subjectName) throws OperatorCreationException, IOException {
        X500Principal principal = new X500Principal("CN=" + subjectName +
                ",OU=User,O=stocks");
        ContentSigner signGen = new JcaContentSignerBuilder("SHA256WithRSA").build(clientKeys.getPrivate());
        PKCS10CertificationRequestBuilder builder =
                new JcaPKCS10CertificationRequestBuilder(principal, clientKeys.getPublic());
        PKCS10CertificationRequest csr = builder.build(signGen);
        PemObject object = new PemObject("CERTIFICATE REQUEST", csr.getEncoded());
        StringWriter writer = new StringWriter();
        PemWriter pemWriter = new PemWriter(writer);
        pemWriter.writeObject(object);
        pemWriter.close();
        return writer.toString();
    }

    private static Certificate convertToCertificate(String pemFile) throws CertificateException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        return factory.generateCertificate(new ByteArrayInputStream(pemFile.getBytes(StandardCharsets.UTF_8)));
    }

}

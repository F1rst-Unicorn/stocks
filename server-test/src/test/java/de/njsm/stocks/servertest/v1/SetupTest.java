package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.servertest.TestSuite;
import io.restassured.RestAssured;
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

import static io.restassured.RestAssured.*;
import static io.restassured.config.SSLConfig.sslConfig;
import static org.hamcrest.CoreMatchers.equalTo;
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
                .trustStore(keystore));
    }

    @After
    public void tearDown() {
        RestAssured.config = RestAssured.config().sslConfig(sslConfig()
                .allowAllHostnames()
                .trustStore(keystore)
                .keyStore("keystore_test", PASSWORD));
    }

    @Test
    public void setupFirstAccount() throws Exception {
        clientKeys = generateKeyPair();
        String csr = getCsr(clientKeys, subjectName);
        Ticket ticket = new Ticket(1, "0000", csr);

        ValidatableResponse response =
                given().
                contentType(ContentType.JSON).
                body(ticket).
        when().
                post("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/uac/newuser").
        then().
                statusCode(200).
                contentType(ContentType.JSON)
                .body("deviceId", equalTo(1))
                .body("pemFile", not(equalTo("")));

        String rawCert = response.extract().body().jsonPath().getString("pemFile");
        storeToDisk(keystore, rawCert, "keystore_test", clientKeys);

    }

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(1024);
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

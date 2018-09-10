package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.common.data.UserDevice;
import de.njsm.stocks.servertest.TestSuite;
import de.njsm.stocks.servertest.v1.DeviceTest;
import de.njsm.stocks.servertest.v1.SetupTest;
import de.njsm.stocks.servertest.v1.UserTest;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.security.KeyPair;
import java.security.KeyStore;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RegistrationTest {

    private static String ticket;

    private static int userId;

    private static int deviceId;

    private static KeyPair keypair;

    private static String commonName;

    private static KeyStore keystore;

    @BeforeClass
    public static void setupCredentials() throws Exception {
        userId = UserTest.createNewUser("Jon");
        Ticket ticket = DeviceTest.createNewDevice("Laptop", userId);
        RegistrationTest.ticket = ticket.ticket;
        RegistrationTest.deviceId = ticket.deviceId;

        keypair = SetupTest.generateKeyPair();
        commonName = "Jon$" + userId + "$Laptop$" + deviceId;
    }

    @AfterClass
    public static void removeKeystore() {
        new File("keystore_2").deleteOnExit();
    }

    @Test
    public void test1cannotRegisterWithWrongTicket() throws Exception {
        tryFailingRegistration(deviceId, "0000", commonName);
        tryFailingRegistration(deviceId, "", commonName);
    }

    @Test
    public void test1cannotRegisterWithWrongDeviceId() throws Exception {
        tryFailingRegistration(1, ticket, commonName);
    }

    @Test
    public void test1cannotRegisterWithWrongCommonName() throws Exception {
        tryFailingRegistration(deviceId, ticket, "Jon$" + userId + "$Laptop$0");
        tryFailingRegistration(deviceId, ticket, "Jon$" + userId + "$Lapto$" + deviceId);
        tryFailingRegistration(deviceId, ticket, "Jack$" + userId + "$Laptop$" + deviceId);
        tryFailingRegistration(deviceId, ticket, "Jon$0$Laptop$" + deviceId);
        tryFailingRegistration(deviceId, ticket, "");
    }

    @Test
    public void test2haveCorrectRegistration() throws Exception {
        String cert = registerSuccessfully(deviceId, ticket, commonName);
        keystore = SetupTest.getFirstKeystore();
        SetupTest.storeToDisk(keystore, cert, "keystore_2", keypair);

        accessServerWithSecondAccount(keystore)
                .statusCode(200)
                .contentType(ContentType.JSON);

        revokedUserCannotAccessAnyMore();
    }

    private void revokedUserCannotAccessAnyMore() throws InterruptedException {
        given()
                .contentType(ContentType.JSON)
                .body(new UserDevice(deviceId, "", 0)).
        when()
                .put(TestSuite.DOMAIN + "/device/remove").
        then()
                .statusCode(204);
        Thread.sleep(3000);

        accessServerWithSecondAccount(keystore)
                .statusCode(400);
    }

    public static ValidatableResponse accessServerWithSecondAccount(KeyStore keystore) {
        return given()
                .config(RestAssuredConfig.config().sslConfig(SSLConfig.sslConfig()
                        .allowAllHostnames()
                        .trustStore(keystore)
                        .keyStore("keystore_2", SetupTest.PASSWORD))).
        when()
                .get(TestSuite.DOMAIN + "/location").
        then();
    }

    private void tryFailingRegistration(int deviceId, String ticket, String commonName) throws Exception {
        register(deviceId, ticket, commonName)
                .body("pemFile", isEmptyOrNullString());
    }

    private String registerSuccessfully(int deviceId, String ticket, String commonName) throws Exception {
        return register(deviceId, ticket, commonName)
                .body("pemFile", not(isEmptyOrNullString()))
                .extract()
                .jsonPath()
                .getString("pemFile");
    }

    private ValidatableResponse register(int deviceId, String ticket, String commonName) throws Exception {
        String csr = SetupTest.getCsr(keypair, commonName);
        return
        given()
                .queryParam("device", deviceId)
                .queryParam("token", ticket)
                .queryParam("csr", csr).
        when()
                .post("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/v2/auth/newuser").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON);

    }
}

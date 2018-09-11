package de.njsm.stocks.servertest.v1;

import de.njsm.stocks.common.data.Ticket;
import de.njsm.stocks.servertest.TestSuite;
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
        Ticket ticket = DeviceTest.createNewDevice("Laptop2", userId);
        RegistrationTest.ticket = ticket.ticket;
        RegistrationTest.deviceId = ticket.deviceId;

        keypair = SetupTest.generateKeyPair();
        commonName = "Jon$" + userId + "$Laptop2$" + deviceId;
    }

    @AfterClass
    public static void removeKeystore() {
        new File("keystore_2").deleteOnExit();
    }

    @Test
    public void haveCorrectRegistration() throws Exception {
        String cert = registerSuccessfully(deviceId, ticket, commonName);
        keystore = SetupTest.getFirstKeystore();
        SetupTest.storeToDisk(keystore, cert, "keystore_2", keypair);

        de.njsm.stocks.servertest.v2.RegistrationTest.accessServerWithSecondAccount(keystore)
                .statusCode(200)
                .contentType(ContentType.JSON);

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
        return given()
                .contentType(ContentType.JSON)
                .body(new Ticket(deviceId, ticket, csr)).
                        when()
                .post("https://" + TestSuite.HOSTNAME + ":" + TestSuite.INIT_PORT + "/uac/newuser").
                        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON);

    }
}

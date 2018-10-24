package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import groovy.lang.Tuple2;
import io.restassured.http.ContentType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Cleanup {

    @Test
    public void cleanFoodItems() {
        List<Integer> ids = getIds("/v2/fooditem");

        for (int id : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", id)
                    .queryParam("version", 0).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/fooditem").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanEan() {
        List<Integer> ids = getIds("/v2/ean");

        for (int id : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", id)
                    .queryParam("version", 0).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/ean").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanFood() {
        List<Integer> ids = getIds("/v2/food");

        for (int id : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", id)
                    .queryParam("version", 0).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/food").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanLocations() {
        List<Integer> ids = getIds("/v2/location");

        for (int id : ids) {
            given()
                    .log().ifValidationFails()
                    .queryParam("id", id)
                    .queryParam("version", 0).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/location").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanDevices() {
        List<Integer> ids = getIds("/v2/device");

        for (int id : ids) {
            if (id == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", id)
                    .queryParam("version", 0).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/device").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void cleanUsers() {
        List<Integer> ids = getIds("/v2/user");

        for (int id : ids) {
            if (id == 1) continue;
            given()
                    .log().ifValidationFails()
                    .queryParam("id", id)
                    .queryParam("version", 0).
            when()
                    .delete(TestSuite.DOMAIN + "/v2/user").
            then()
                    .log().ifValidationFails()
                    .statusCode(200)
                    .contentType(ContentType.JSON);
        }
    }

    @Test
    public void setupOtherTestAccounts() throws IOException {
        Tuple2<Integer, String> ticket1 = DeviceTest.createNewDevice("cli-client", 1);
        Tuple2<Integer, String> ticket2 = DeviceTest.createNewDevice("android-client", 1);

        writeToFile("target/01_ticket", ticket1.getSecond());
        writeToFile("target/01_id", String.valueOf(ticket1.getFirst()));
        writeToFile("target/02_ticket", ticket2.getSecond());
        writeToFile("target/02_id", String.valueOf(ticket2.getFirst()));

    }

    private void writeToFile(String filename, String content) throws IOException {
        FileWriter writer = new FileWriter(filename);
        writer.write(content);
        writer.close();
    }

    private List<Integer> getIds(String path) {
        return when()
                .get(TestSuite.DOMAIN + path).
        then()
                .log().ifValidationFails()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList("data.id", Integer.class);
    }
}

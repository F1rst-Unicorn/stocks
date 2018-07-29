package de.njsm.stocks.servertest;

import de.njsm.stocks.common.data.*;
import io.restassured.http.ContentType;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.threeten.bp.Instant;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Cleanup {

    @Test
    public void cleanFoodItems() {
        List<Integer> ids = getIds("/food/fooditem");

        for (int i : ids) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new FoodItem(i, Instant.EPOCH, 0, 0, 0, 0)).
            when()
                    .put(TestSuite.DOMAIN + "/food/fooditem/remove").
            then()
                    .statusCode(204);
        }
    }

    @Test
    public void cleanEan() {
        List<Integer> ids = getIds("/ean");

        for (int i : ids) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new EanNumber(i, "", 0)).
            when()
                    .put(TestSuite.DOMAIN + "/ean/remove").
            then()
                    .statusCode(204);
        }
    }

    @Test
    public void cleanFood() {
        List<Integer> ids = getIds("/food");

        for (int i : ids) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new Food(i, "")).
            when()
                    .put(TestSuite.DOMAIN + "/food/remove").
            then()
                    .statusCode(204);
        }
    }

    @Test
    public void cleanLocations() {
        List<Integer> ids = getIds("/location");

        for (int i : ids) {
            given()
                    .contentType(ContentType.JSON)
                    .body(new Location(i, "")).
            when()
                    .put(TestSuite.DOMAIN + "/location/remove").
            then()
                    .statusCode(204);
        }
    }

    @Test
    public void cleanDevices() {
        List<Integer> ids = getIds("/device");

        for (int i : ids) {
            if (i == 1) continue;
            given()
                    .contentType(ContentType.JSON)
                    .body(new Location(i, "")).
            when()
                    .put(TestSuite.DOMAIN + "/device/remove").
            then()
                    .statusCode(204);
        }
    }

    @Test
    public void cleanUsers() {
        List<Integer> ids = getIds("/user");

        for (int i : ids) {
            if (i == 1) continue;
            given()
                    .contentType(ContentType.JSON)
                    .body(new Location(i, "")).
            when()
                    .put(TestSuite.DOMAIN + "/user/remove").
            then()
                    .statusCode(204);
        }
    }

    @Test
    public void setupOtherTestAccounts() throws IOException {
        Ticket ticket1 = DeviceTest.createNewDevice("Device", 1);
        Ticket ticket2 = DeviceTest.createNewDevice("Device", 1);

        writeToFile("target/01_ticket", ticket1.ticket);
        writeToFile("target/01_id", String.valueOf(ticket1.id));
        writeToFile("target/02_ticket", ticket2.ticket);
        writeToFile("target/02_id", String.valueOf(ticket2.id));

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
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract()
                .jsonPath()
                .getList("id", Integer.class);
    }
}

package de.njsm.stocks.servertest.v2;

import de.njsm.stocks.servertest.TestSuite;
import io.restassured.http.ContentType;
import org.junit.Test;

import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

public class FoodTest {

    @Test
    public void addAnItem() {
        when()
                .put(TestSuite.DOMAIN + "/v2/food?name=Carrot").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0));

        when()
                .get(TestSuite.DOMAIN + "/v2/food").
        then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("status", equalTo(0))
                .body("data.name", hasItem("Carrot"));
    }
}

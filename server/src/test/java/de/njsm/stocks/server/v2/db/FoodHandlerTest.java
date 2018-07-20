package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.Config;
import de.njsm.stocks.server.v1.internal.db.DatabaseHelper;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FoodHandlerTest {

    private FoodHandler uut;

    private static int resourceCounter = 0;

    @Before
    public void resetDatabase() throws SQLException {
        DatabaseHelper.resetSampleData();

        Config c = new Config(System.getProperties());
        uut = new FoodHandler(String.format("jdbc:mariadb://%s:%s/%s?useLegacyDatetimeCode=false&serverTimezone=+00:00",
                c.getDbAddress(), c.getDbPort(), c.getDbName()),
                c.getDbUsername(),
                c.getDbPassword(),
                "hystrix group " + String.valueOf(resourceCounter),
                new InsertVisitor<>());
        resourceCounter++;
    }

    @Test
    public void addAFood() {
        Food data = new Food(7, "Banana", 1);

        StatusCode code = uut.add(data);

        assertEquals(StatusCode.SUCCESS, code);

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().stream().map(f -> f.name).anyMatch(name -> name.equals("Banana")));

    }
}
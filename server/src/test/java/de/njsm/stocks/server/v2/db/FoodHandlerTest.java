package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Food;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FoodHandlerTest extends DbTestCase {

    private FoodHandler uut;

    @Before
    public void setup() {
        uut = new FoodHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                new InsertVisitor<>());
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

    @Test
    public void renameAFood() {
        Food data = new Food(2, "Beer", 0);

        StatusCode result = uut.rename(data, "Wine");

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertTrue(dbData.success().stream().map(f -> f.name).anyMatch(name -> name.equals("Wine")));
    }

    @Test
    public void wrongVersionIsNotRenamed() {
        Food data = new Food(2, "Beer", 100);

        StatusCode result = uut.rename(data, "Wine");

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void unknownIsReported() {
        Food data = new Food(100, "Beer", 1);

        StatusCode result = uut.rename(data, "Wine");

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deleteAFood() {
        Food data = new Food(2, "Beer", 0);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(2, dbData.success().size());
        assertTrue(dbData.success().stream().map(f -> f.name).noneMatch(name -> name.equals("Beer")));
    }

    @Test
    public void invalidDataVersionIsRejected() {
        Food data = new Food(2, "Beer", 100);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);

        Validation<StatusCode, List<Food>> dbData = uut.get();

        assertTrue(dbData.isSuccess());

        assertEquals(3, dbData.success().size());
    }

    @Test
    public void unknownDeletionsAreReported() {
        Food data = new Food(100, "Beer", 1);

        StatusCode result = uut.delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }
}
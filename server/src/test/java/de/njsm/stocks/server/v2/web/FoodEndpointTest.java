package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;

import static de.njsm.stocks.server.v2.business.StatusCode.SUCCESS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class FoodEndpointTest {

    private FoodEndpoint uut;

    private FoodHandler dbLayer;

    @Before
    public void setup() throws Exception {
        dbLayer = Mockito.mock(FoodHandler.class);
        uut = new FoodEndpoint(dbLayer);
    }

    @After
    public void tearDown() throws Exception {
        Mockito.verifyNoMoreInteractions(dbLayer);
    }

    @Test
    public void foodIsAdded() {
        Food data = new Food(0, "Banana", 1);
        when(dbLayer.add(data)).thenReturn(SUCCESS);

        Response response = uut.putFood(data.name);

        assertEquals(SUCCESS, response.status);
        verify(dbLayer).add(data);
    }

    @Test
    public void getFoodReturnsList() {
        List<Food> data = Collections.singletonList(new Food(2, "Banana", 2));
        when(dbLayer.get()).thenReturn(Validation.success(data));

        ListResponse<Food> response = uut.getFood();

        assertEquals(SUCCESS, response.status);
        assertEquals(data, response.data);
        verify(dbLayer).get();
    }

    @Test
    public void renameFoodWorks() {
        Food data = new Food(1, "", 2);
        String newName = "Bread";
        when(dbLayer.rename(data, newName)).thenReturn(SUCCESS);

        Response response = uut.renameFood(data.id, data.version, newName);

        assertEquals(SUCCESS, response.status);
        verify(dbLayer).rename(data, newName);
    }

    @Test
    public void deleteFoodWorks() {
        Food data = new Food(1, "", 2);
        when(dbLayer.delete(data)).thenReturn(SUCCESS);

        Response response = uut.deleteFood(data.id, data.version);

        assertEquals(SUCCESS, response.status);
        verify(dbLayer).delete(data);
    }
}
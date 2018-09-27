package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class FoodItemEndpointTest {

    private static final String DATE = "1970.01.01-00:00:00.000-+0000";

    private FoodItemEndpoint uut;

    private FoodItemHandler databaseHandler;

    @Before
    public void setup() {
        databaseHandler = Mockito.mock(FoodItemHandler.class);
        uut = new FoodItemEndpoint(databaseHandler);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(databaseHandler);
    }

    @Test
    public void testGettingItems() {
        Mockito.when(databaseHandler.get()).thenReturn(Validation.success(new LinkedList<>()));

        ListResponse<FoodItem> result = uut.getItems();

        assertEquals(StatusCode.SUCCESS, result.status);
        assertEquals(0, result.data.size());
        Mockito.verify(databaseHandler).get();
    }

    @Test
    public void insertInvalidLocationIdIsReported() throws IOException {

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), DATE, 0, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void insertInvalidTypeIdIsReported() throws IOException {

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), DATE, 2, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void insertInvalidExpirationIdIsReported() throws IOException {

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), "invalid date", 2, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validInsertHappens() throws IOException {
        FoodItem expected = new FoodItem(0, 0, Instant.EPOCH, 2, 2,
                PrincipalFilterTest.TEST_USER.getDid(),
                PrincipalFilterTest.TEST_USER.getUid());
        Mockito.when(databaseHandler.add(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.putItem(BaseTestEndpoint.createMockRequest(), DATE, expected.storedIn, expected.ofType);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(databaseHandler).add(expected);
    }

    @Test
    public void editInvalidIdIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 0, 2, DATE, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void editInvalidVersionIdIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 1, -1, DATE, 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void editInvalidDateIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 2, 2, "invalid", 2);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void editInvalidLocationIsReported() throws IOException {

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(), 2, 2, DATE, 0);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validEditingHappens() throws IOException {
        FoodItem expected = new FoodItem(2, 2, Instant.EPOCH, 0, 2,
                PrincipalFilterTest.TEST_USER.getDid(),
                PrincipalFilterTest.TEST_USER.getUid());
        Mockito.when(databaseHandler.edit(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.editItem(BaseTestEndpoint.createMockRequest(),
                expected.id, expected.version, DATE, expected.storedIn);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(databaseHandler).edit(expected);
    }

    @Test
    public void deleteInvalidIdIsReported() {

        Response result = uut.deleteItem(0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deleteInvalidVersionIsReported() {

        Response result = uut.deleteItem(1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validDeletionHappens() {
        FoodItem expected = new FoodItem(2, 2, Instant.EPOCH, 0, 0, 0, 0);
        Mockito.when(databaseHandler.delete(expected)).thenReturn(StatusCode.SUCCESS);

        Response result = uut.deleteItem(expected.id, expected.version);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(databaseHandler).delete(expected);
    }
}
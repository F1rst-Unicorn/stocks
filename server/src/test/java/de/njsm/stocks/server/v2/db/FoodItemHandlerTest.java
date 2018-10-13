package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.business.data.UserDevice;
import fj.data.Validation;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.time.Instant;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class FoodItemHandlerTest extends DbTestCase {

    private FoodItemHandler uut;

    private PresenceChecker<UserDevice> userDevicePresenceChecker;

    @Before
    public void setup() {
        userDevicePresenceChecker = (PresenceChecker<UserDevice>) Mockito.mock(PresenceChecker.class);

        uut = new FoodItemHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                new InsertVisitor<>(),
                userDevicePresenceChecker);
    }

    @After
    public void verifyMocks() {
        Mockito.verifyNoMoreInteractions(userDevicePresenceChecker);
    }

    @Test
    public void testInserting() {
        FoodItem item = new FoodItem(1, 0, Instant.EPOCH, 2, 1, 1, 1);

        Validation<StatusCode, Integer> result = uut.add(item);

        Validation<StatusCode, List<FoodItem>> items = uut.get();
        Assert.assertTrue(result.isSuccess());
        assertTrue(items.isSuccess());
        assertEquals(4, items.success().size());
    }

    @Test
    public void testGettingItems() {

        Validation<StatusCode, List<FoodItem>> result = uut.get();

        assertTrue(result.isSuccess());
        assertEquals(3, result.success().size());
        assertEquals(new FoodItem(1, 0, Instant.EPOCH, 2, 1, 3, 2), result.success().get(0));
        assertEquals(new FoodItem(2, 0, Instant.EPOCH, 2, 1, 3, 2), result.success().get(1));
        assertEquals(new FoodItem(3, 0, Instant.EPOCH, 2, 1, 3, 2), result.success().get(2));
    }

    @Test
    public void deletingUnknownIsReported() {

        StatusCode result = uut.delete(new FoodItem(4, 0));

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingWrongVersionIsReported() {

        StatusCode result = uut.delete(new FoodItem(1, 99));

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void validDeletionHappens() {

        StatusCode result = uut.delete(new FoodItem(1, 0));

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, List<FoodItem>> items = uut.get();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.isSuccess());
        assertEquals(2, items.success().size());
    }

    @Test
    public void validEditingHappens() {
        FoodItem item = new FoodItem(1, 0, Instant.ofEpochMilli(42), 2, 2, 3, 2);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.SUCCESS, result);
        Validation<StatusCode, List<FoodItem>> items = uut.get();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.isSuccess());
        assertEquals(3, items.success().size());
        item.version++;
        assertTrue(items.success().contains(item));
    }

    @Test
    public void editingWrongVersionIsReported() {
        FoodItem item = new FoodItem(1, 99, Instant.EPOCH, 2, 2, 3, 2);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void editingUnknownIdIsReported() {
        FoodItem item = new FoodItem(99, 0, Instant.EPOCH, 2, 2, 3, 2);

        StatusCode result = uut.edit(item);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void movingFromUnknownDeviceIsReported() {
        UserDevice from = new UserDevice(1, 0, "fdsa", 1);
        UserDevice to = new UserDevice(3, 0, "fdsa", 2);
        Mockito.when(userDevicePresenceChecker.isMissing(eq(from), any())).thenReturn(true);
        Mockito.when(userDevicePresenceChecker.isMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isMissing(eq(from), any());
    }

    @Test
    public void movingToUnknownDeviceIsReported() {
        UserDevice from = new UserDevice(1, 0, "fdsa", 1);
        UserDevice to = new UserDevice(3, 0, "fdsa", 2);
        Mockito.when(userDevicePresenceChecker.isMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isMissing(eq(to), any())).thenReturn(true);

        StatusCode result = uut.transferFoodItems(from, to);

        assertEquals(StatusCode.NOT_FOUND, result);
        Mockito.verify(userDevicePresenceChecker).isMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isMissing(eq(to), any());
    }

    @Test
    public void successfulTransfer() {
        UserDevice from = new UserDevice(3, 0, "fdsa", 2);
        UserDevice to = new UserDevice(1, 0, "fdsa", 1);
        Mockito.when(userDevicePresenceChecker.isMissing(eq(from), any())).thenReturn(false);
        Mockito.when(userDevicePresenceChecker.isMissing(eq(to), any())).thenReturn(false);

        StatusCode result = uut.transferFoodItems(from, to);

        List<FoodItem> items = uut.get().success();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(items.stream().allMatch(item -> (item.version == 1) == (item.registers == to.id)));
        Mockito.verify(userDevicePresenceChecker).isMissing(eq(from), any());
        Mockito.verify(userDevicePresenceChecker).isMissing(eq(to), any());
    }
}
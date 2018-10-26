package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.LocationFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LocationEndpointTest extends BaseTestEndpoint {

    private Location testItem;

    private LocationEndpoint uut;

    private DatabaseHandler handler;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        uut = new LocationEndpoint(handler);

        testItem = new Location(1, "Fridge");
        Mockito.when(handler.get(LocationFactory.f))
                .thenReturn(new Data[0]);
    }

    @Test
    public void testGettingLocations() {
        Data[] result = uut.getLocations(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(LocationFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingLocation() {
        uut.addLocation(createMockRequest(), testItem);

        Mockito.verify(handler).add(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void idIsClearedByServer() {
        Location data = new Location(3, "123-123-123");
        Location expected = new Location(0, "123-123-123");

        uut.addLocation(createMockRequest(), data);

        Mockito.verify(handler).add(expected);
    }


    @Test
    public void testRenamingLocation() {
        String newLocation = "Basement";
        uut.renameLocation(createMockRequest(), testItem, newLocation);

        Mockito.verify(handler).rename(testItem, newLocation);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testRemovingLocation() {
        uut.removeLocation(createMockRequest(), testItem);

        Mockito.verify(handler).remove(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

}

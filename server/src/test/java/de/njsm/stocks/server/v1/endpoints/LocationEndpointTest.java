package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.Location;
import de.njsm.stocks.common.data.LocationFactory;
import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;

public class LocationEndpointTest extends BaseTestEndpoint {

    private Location testItem;

    private LocationEndpoint uut;

    private DatabaseHandler handler;

    private UserContextFactory authAdmin;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(UserContextFactory.class);
        uut = new LocationEndpoint(handler, authAdmin);

        testItem = new Location(1, "Fridge");
        Mockito.when(handler.get(LocationFactory.f))
                .thenReturn(new Data[0]);
        Mockito.when(authAdmin.getPrincipals(any()))
                .thenReturn(TEST_USER);
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

package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class LocationEndpointTest extends BaseTestEndpoint {

    private Config c;
    private Location testItem;

    private LocationEndpoint uut;

    @Before
    public void setup() {
        c = new MockConfig(System.getProperties());
        Mockito.when(c.getDbHandler().get(LocationFactory.f))
                .thenReturn(new Data[0]);
        testItem = new Location(1, "Fridge");

        uut = new LocationEndpoint(c);
    }

    @Test
    public void testGettingLocations() {
        Data[] result = uut.getLocations(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(c.getDbHandler()).get(LocationFactory.f);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingLocation() {
        Assert.assertTrue(HttpsUserContextFactory.isNameValid(testItem.name));

        uut.addLocation(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).add(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRenamingLocation() {
        String newLocation = "Basement";
        uut.renameLocation(createMockRequest(), testItem, newLocation);

        Mockito.verify(c.getDbHandler()).rename(testItem, newLocation);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRemovingLocation() {
        uut.removeLocation(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).remove(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

}

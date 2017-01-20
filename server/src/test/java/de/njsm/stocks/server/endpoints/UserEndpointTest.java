package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.server.data.*;
import de.njsm.stocks.server.internal.Config;
import de.njsm.stocks.server.internal.MockConfig;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class UserEndpointTest extends BaseTestEndpoint {

    private Config c;
    private String ticket;
    private User testItem;
    private User invalidTestItem;

    private UserEndpoint uut;

    @Before
    public void setup() {
        c = new MockConfig(System.getProperties());
        Mockito.when(c.getDbHandler().get(UserFactory.f))
                .thenReturn(new Data[0]);
        testItem = new User(1, "John");
        invalidTestItem = new User(1, "John$1");

        uut = new UserEndpoint(c);
    }

    @Test
    public void testGettingUsers() {
        Data[] result = uut.getUsers(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(c.getDbHandler()).get(UserFactory.f);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingValidItem() {
        Assert.assertTrue(HttpsUserContextFactory.isNameValid(testItem.name));

        uut.addUser(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).add(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testAddingInvalidItem() {
        Assert.assertFalse(HttpsUserContextFactory.isNameValid(invalidTestItem.name));

        uut.addUser(createMockRequest(), invalidTestItem);

        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

    @Test
    public void testRemovingUsers() {
        uut.removeUser(createMockRequest(), testItem);

        Mockito.verify(c.getDbHandler()).removeUser(testItem);
        Mockito.verifyNoMoreInteractions(c.getDbHandler());
    }

}

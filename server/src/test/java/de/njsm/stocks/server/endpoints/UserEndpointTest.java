package de.njsm.stocks.server.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.common.data.UserFactory;
import de.njsm.stocks.server.internal.auth.HttpsUserContextFactory;
import de.njsm.stocks.server.internal.auth.UserContextFactory;
import de.njsm.stocks.server.internal.db.DatabaseHandler;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.any;

public class UserEndpointTest extends BaseTestEndpoint {

    private User testItem;
    private User invalidTestItem;

    private DatabaseHandler handler;

    private UserContextFactory authAdmin;

    private UserEndpoint uut;

    @Before
    public void setup() {
        handler = Mockito.mock(DatabaseHandler.class);
        authAdmin = Mockito.mock(UserContextFactory.class);
        uut = new UserEndpoint(handler, authAdmin);

        Mockito.when(handler.get(UserFactory.f))
                .thenReturn(new Data[0]);
        Mockito.when(authAdmin.getPrincipals(any()))
                .thenReturn(TEST_USER);
        testItem = new User(1, "John");
        invalidTestItem = new User(1, "John$1");
    }

    @Test
    public void testGettingUsers() {
        Data[] result = uut.getUsers(createMockRequest());

        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.length);
        Mockito.verify(handler).get(UserFactory.f);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingValidItem() {
        Assert.assertTrue(HttpsUserContextFactory.isNameValid(testItem.name));

        uut.addUser(createMockRequest(), testItem);

        Mockito.verify(handler).add(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testAddingInvalidItem() {
        Assert.assertFalse(HttpsUserContextFactory.isNameValid(invalidTestItem.name));

        uut.addUser(createMockRequest(), invalidTestItem);

        Mockito.verifyNoMoreInteractions(handler);
    }

    @Test
    public void testRemovingUsers() {
        uut.removeUser(createMockRequest(), testItem);

        Mockito.verify(handler).removeUser(testItem);
        Mockito.verifyNoMoreInteractions(handler);
    }

}

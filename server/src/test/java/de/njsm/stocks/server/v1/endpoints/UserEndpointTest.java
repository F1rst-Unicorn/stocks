package de.njsm.stocks.server.v1.endpoints;

import de.njsm.stocks.common.data.Data;
import de.njsm.stocks.common.data.User;
import de.njsm.stocks.server.v1.internal.business.UserContextFactory;
import de.njsm.stocks.server.v1.internal.business.UserManager;
import de.njsm.stocks.server.v1.internal.db.DatabaseHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;

public class UserEndpointTest extends BaseTestEndpoint {

    private DatabaseHandler handler;

    private UserManager userManager;

    private UserEndpoint uut;

    @Before
    public void setup() {
        userManager = Mockito.mock(UserManager.class);
        handler = Mockito.mock(DatabaseHandler.class);
        UserContextFactory contextFactory = Mockito.mock(UserContextFactory.class);
        uut = new UserEndpoint(userManager, handler, contextFactory);

        Mockito.when(userManager.getUsers())
                .thenReturn(new Data[0]);
        Mockito.when(contextFactory.getPrincipals(any()))
                .thenReturn(TEST_USER);
    }


    @Test
    public void testAddingDevice() {
        User user = new User(0, "Jack");

        uut.addUser(BaseTestEndpoint.createMockRequest(), user);

        Mockito.verify(userManager).addUser(user);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(userManager);
    }

    @Test
    public void idIsClearedByServer() {
        User data = new User(3, "123-123-123");
        User expected = new User(0, "123-123-123");

        uut.addUser(createMockRequest(), data);

        Mockito.verify(userManager).addUser(expected);
    }


    @Test
    public void testGettingDevices() {

        Data[] output = uut.getUsers(BaseTestEndpoint.createMockRequest());

        assertEquals(0, output.length);
        Mockito.verify(userManager).getUsers();
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(userManager);

    }

    @Test
    public void testRemovingDevice() {
        User user = new User(0, "Jack");

        uut.removeUser(BaseTestEndpoint.createMockRequest(), user);

        Mockito.verify(userManager).removeUser(user);
        Mockito.verifyNoMoreInteractions(handler);
        Mockito.verifyNoMoreInteractions(userManager);
    }

}

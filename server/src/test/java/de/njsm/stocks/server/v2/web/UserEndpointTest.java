package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v1.endpoints.BaseTestEndpoint;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.UserManager;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.web.data.ListResponse;
import de.njsm.stocks.server.v2.web.data.Response;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.Matchers.any;

public class UserEndpointTest {

    private UserEndpoint uut;

    private UserManager userManager;

    @Before
    public void setup() {
        userManager = Mockito.mock(UserManager.class);

        uut = new UserEndpoint(userManager);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(userManager);
    }

    @Test
    public void getUsers() {
        Mockito.when(userManager.get()).thenReturn(Validation.success(Collections.emptyList()));

        ListResponse<User> result = uut.getUsers();

        assertEquals(StatusCode.SUCCESS, result.status);
        assertEquals(Collections.emptyList(), result.data);
        Mockito.verify(userManager).get();
    }

    @Test
    public void addingInvalidNameIsRejected() {

        Response result = uut.putUser(null);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validAddingIsSuccessful() {
        String name = "user";
        Mockito.when(userManager.addUser(any())).thenReturn(StatusCode.SUCCESS);

        Response result = uut.putUser(name);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(userManager).addUser(new User(name));
    }

    @Test
    public void deletingInvalidVersionIsRejected() {

        Response result = uut.deleteUser(BaseTestEndpoint.createMockRequest(),
                1, -1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void deletingInvalidIdIsRejected() {

        Response result = uut.deleteUser(BaseTestEndpoint.createMockRequest(),
                0, 1);

        assertEquals(StatusCode.INVALID_ARGUMENT, result.status);
    }

    @Test
    public void validDeletingIsSuccessful() {
        Mockito.when(userManager.deleteUser(any(), any())).thenReturn(StatusCode.SUCCESS);

        Response result = uut.deleteUser(BaseTestEndpoint.createMockRequest(), 1, 2);

        assertEquals(StatusCode.SUCCESS, result.status);
        Mockito.verify(userManager).deleteUser(new User(1, 2), PrincipalFilterTest.TEST_USER);
    }
}
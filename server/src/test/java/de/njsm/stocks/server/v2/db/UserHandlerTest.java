package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.User;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class UserHandlerTest extends DbTestCase {

    private UserHandler uut;

    @Before
    public void setup() {
        uut = new UserHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                new InsertVisitor<>());
    }

    @Test
    public void gettingUsersWorks() {

        Validation<StatusCode, List<User>> result = uut.get();

        assertTrue(result.isSuccess());
        assertEquals(3, result.success().size());
        assertThat(result.success(), hasItem(new User(1, 0, "Bob")));
        assertThat(result.success(), hasItem(new User(2, 0, "Alice")));
        assertThat(result.success(), hasItem(new User(3, 0, "Jack")));
    }

    @Test
    public void addingUserWorks() {
        User input = new User(1, 0, "testuser");

        Validation<StatusCode, Integer> result = uut.add(input);

        Validation<StatusCode, List<User>> users = uut.get();
        assertTrue(result.isSuccess());
        assertEquals(new Integer(4), result.success());
        assertTrue(users.isSuccess());
        assertEquals(4, users.success().size());
        input.id = 4;
        assertThat(users.success(), hasItem(input));
    }

    @Test
    public void deletingUnknownIdIsReported() {

        StatusCode result = uut.delete(new User(99999, 0, ""));

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void deletingInvalidVersionIsReported() {

        StatusCode result = uut.delete(new User(1, 999, ""));

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void deletingValidDeviceWorks() {
        User input = new User(1, 0, "user");

        StatusCode result = uut.delete(input);

        Validation<StatusCode, List<User>> users = uut.get();
        assertEquals(StatusCode.SUCCESS, result);
        assertTrue(users.isSuccess());
        assertEquals(2, users.success().size());
        assertThat(users.success(), not(hasItem(input)));
    }

}
package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class PrincipalFilterTest {

    private PrincipalFilter uut;

    private ContainerRequestContext context;

    public static final String USER_STRING = "/CN=John$5$Mobile$1";

    public static final Principals TEST_USER = new Principals("John", "Mobile", 5, 1);

    @Before
    public void setup() {
        context = Mockito.mock(ContainerRequestContext.class);
        uut = new PrincipalFilter();
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(context);
    }

    @Test
    public void sentryRequestsAreIgnored() {
        UriInfo info = Mockito.mock(UriInfo.class);
        when(context.getHeaderString(PrincipalFilter.ORIGIN)).thenReturn(PrincipalFilter.ORIGIN_SENTRY);
        when(context.getMethod()).thenReturn("GET");
        when(context.getUriInfo()).thenReturn(info);

        uut.filter(context);

        verify(context).getHeaderString(PrincipalFilter.ORIGIN);
        verify(context).getMethod();
        verify(context).getUriInfo();
    }

    @Test
    public void serverRequestsAreInvestigated() {
        UriInfo info = Mockito.mock(UriInfo.class);
        when(info.getPath()).thenReturn("/foo/bar");
        when(context.getHeaderString(PrincipalFilter.ORIGIN)).thenReturn("server");
        when(context.getHeaderString(PrincipalFilter.SSL_CLIENT_KEY)).thenReturn(USER_STRING);
        when(context.getMethod()).thenReturn("GET");
        when(context.getUriInfo()).thenReturn(info);

        uut.filter(context);

        verify(context).getHeaderString(PrincipalFilter.ORIGIN);
        verify(context).getHeaderString(PrincipalFilter.SSL_CLIENT_KEY);
        verify(context).setProperty(PrincipalFilter.STOCKS_PRINCIPAL, TEST_USER);
        verify(context).getMethod();
        verify(context).getUriInfo();
    }

    @Test
    public void testParseCorrectName() {
        int uid = 3;
        int did = 6;
        String[] testInput = new String[] {
                "my_username",
                String.valueOf(uid),
                "my_device_name",
                String.valueOf(did)};
        String input = "CN=";
        for (int i = 0; i < testInput.length-1; i++){
            input = input.concat(testInput[i] + "$");
        }
        input = input.concat(testInput[testInput.length-1]);

        Validation<StatusCode, Principals> p = PrincipalFilter.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals(testInput[0], p.success().getUsername());
        assertEquals(uid, p.success().getUid());
        assertEquals(testInput[2], p.success().getDeviceName());
        assertEquals(did, p.success().getDid());

    }

    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        Validation<StatusCode, Principals> p = PrincipalFilter.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals("", p.success().getUsername());
        assertEquals("", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(1, p.success().getDid());
    }

    @Test
    public void testEmptyNameNoSlashes() {
        String input = "CN=$1$$1";

        Validation<StatusCode, Principals> p = PrincipalFilter.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals("", p.success().getUsername());
        assertEquals("", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(1, p.success().getDid());
    }

    @Test
    public void testNameWithSpacesAndSpecialCharacters() {
        String input = "CN=John Doe$1$my-test_device$1";

        Validation<StatusCode, Principals> p = PrincipalFilter.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals("John Doe", p.success().getUsername());
        assertEquals("my-test_device", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(1, p.success().getDid());
    }

    public void testMalformed() {
        String input = "/CN=$1$1";

        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    @Test(expected = SecurityException.class)
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        Validation<StatusCode, Principals> p = PrincipalFilter.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals("", p.success().getUsername());
        assertEquals("", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(1, p.success().getDid());
    }

    @Test(expected = SecurityException.class)
    public void testParseNameWithDollar() {
        String[] testInput = new String[] {"my_user$name", "3",
                "my_device_name", "6"};
        String input = "CN=";
        for (int i = 0; i < testInput.length-1; i++){
            input = input.concat(testInput[i] + "$");
        }
        input = input.concat(testInput[testInput.length-1]);

        PrincipalFilter.parseSubjectName(input);
    }

    public void testTooFewDollars() {
        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName("CN=username$devicename$4");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    public void testCompleteGarbage() {
        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName("29A");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    public void testEmptySubject() {
        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName("");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

}
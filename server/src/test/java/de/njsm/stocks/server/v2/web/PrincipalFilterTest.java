package de.njsm.stocks.server.v2.web;

import com.netflix.hystrix.exception.HystrixBadRequestException;
import de.njsm.stocks.server.util.Principals;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PrincipalFilterTest {

    private PrincipalFilter uut;

    private ContainerRequestContext context;

    public static final String USER_STRING = "/CN=John$5$Mobile$1";

    public static final Principals TEST_USER = new Principals("John", "Mobile", 5, 1);

    @Before
    public void setup() throws Exception {
        context = Mockito.mock(ContainerRequestContext.class);
        uut = new PrincipalFilter();
    }

    @After
    public void tearDown() {
        verifyNoMoreInteractions(context);
    }

    @Test
    public void sentryRequestsAreIgnored() throws IOException {
        when(context.getHeaderString(PrincipalFilter.ORIGIN)).thenReturn(PrincipalFilter.ORIGIN_SENTRY);

        uut.filter(context);

        verify(context).getHeaderString(PrincipalFilter.ORIGIN);
    }

    @Test
    public void serverRequestsAreInvestigated() throws IOException {
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

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals(testInput[0], p.getUsername());
        assertEquals(uid, p.getUid());
        assertEquals(testInput[2], p.getDeviceName());
        assertEquals(did, p.getDid());

    }

    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test
    public void testEmptyNameNoSlashes() {
        String input = "CN=$1$$1";

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test
    public void testNameWithSpacesAndSpecialCharacters() {
        String input = "CN=John Doe$1$my-test_device$1";

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals("John Doe", p.getUsername());
        assertEquals("my-test_device", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
    }

    @Test(expected = HystrixBadRequestException.class)
    public void testMalformed() {
        String input = "/CN=$1$1";

        PrincipalFilter.parseSubjectName(input);

    }

    @Test(expected = SecurityException.class)
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        Principals p = PrincipalFilter.parseSubjectName(input);

        assertEquals("", p.getUsername());
        assertEquals("", p.getDeviceName());
        assertEquals(1, p.getUid());
        assertEquals(1, p.getDid());
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

    @Test(expected = HystrixBadRequestException.class)
    public void testTooFewDollars() {
        PrincipalFilter.parseSubjectName("CN=username$devicename$4");
    }

    @Test(expected = HystrixBadRequestException.class)
    public void testCompleteGarbage() {
        PrincipalFilter.parseSubjectName("29A");
    }


}
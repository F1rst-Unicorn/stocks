/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.web.servlet.PrincipalFilter;
import fj.data.Validation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PrincipalFilterTest {

    private PrincipalFilter uut;

    private ContainerRequestContext context;

    public static final String USER_STRING = "/CN=John$5$Mobile$1";

    public static final Principals TEST_USER = new Principals("John", "Mobile", 5, 1);

    @BeforeEach
    public void setup() {
        context = Mockito.mock(ContainerRequestContext.class);
        uut = new PrincipalFilter();
    }

    @AfterEach
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
    public void invalidServerRequestsAreReported() {
        when(context.getHeaderString(PrincipalFilter.ORIGIN)).thenReturn("server");
        when(context.getHeaderString(PrincipalFilter.SSL_CLIENT_KEY)).thenReturn("$$$$$");

        uut.filter(context);

        verify(context).getHeaderString(PrincipalFilter.ORIGIN);
        verify(context).getHeaderString(PrincipalFilter.SSL_CLIENT_KEY);
        ArgumentCaptor<Response> captor = ArgumentCaptor.forClass(Response.class);
        verify(context).abortWith(captor.capture());
        assertEquals(403, captor.getValue().getStatus());
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

    @Test
    public void testMalformed() {
        String input = "/CN=$1$1";

        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    @Test
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        assertThrows(SecurityException.class, () -> PrincipalFilter.parseSubjectName(input));
    }

    @Test
    public void testParseNameWithDollar() {
        String input = "CN=my_user$name$3$my_device_name$6";

        assertThrows(SecurityException.class, () -> PrincipalFilter.parseSubjectName(input));
    }

    @Test
    public void testTooFewDollars() {
        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName("CN=username$devicename$4");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    @Test
    public void testCompleteGarbage() {
        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName("29A");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    @Test
    public void testEmptySubject() {
        Validation<StatusCode, Principals> result = PrincipalFilter.parseSubjectName("");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

}

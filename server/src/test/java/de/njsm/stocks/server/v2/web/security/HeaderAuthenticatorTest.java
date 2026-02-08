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

package de.njsm.stocks.server.v2.web.security;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import fj.data.Validation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HeaderAuthenticatorTest {

    private HeaderAuthenticator uut;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    private AuthenticationManager authenticationManager;

    public static final String USER_STRING = "/CN=John$5$Mobile$1";

    public static final Principals TEST_USER = new Principals("John", "Mobile", 5, 1);

    @BeforeEach
    public void setup() {
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        filterChain = Mockito.mock(FilterChain.class);
        authenticationManager = Mockito.mock(AuthenticationManager.class);
        uut = new HeaderAuthenticator(authenticationManager);
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(request);
        verifyNoMoreInteractions(response);
        verifyNoMoreInteractions(filterChain);
        verifyNoMoreInteractions(authenticationManager);
    }

    @Test
    public void sentryRequestsAreIgnored() throws Exception {
        when(request.getHeader(HeaderAuthenticator.ORIGIN)).thenReturn(HeaderAuthenticator.ORIGIN_SENTRY);
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/some/path");

        uut.doFilterInternal(request, response, filterChain);

        verify(request).getHeader(HeaderAuthenticator.ORIGIN);
        verify(request).getMethod();
        verify(request).getServletPath();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void serverRequestsAreInvestigated() throws Exception {
        when(request.getHeader(HeaderAuthenticator.ORIGIN)).thenReturn("server");
        when(request.getHeader(HeaderAuthenticator.SSL_CLIENT_KEY)).thenReturn(USER_STRING);
        when(request.getMethod()).thenReturn("GET");
        when(request.getServletPath()).thenReturn("/foo/bar");
        StocksAuthentication authentication = new StocksAuthentication(TEST_USER);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);

        uut.doFilterInternal(request, response, filterChain);

        verify(request).getHeader(HeaderAuthenticator.ORIGIN);
        verify(request).getHeader(HeaderAuthenticator.SSL_CLIENT_KEY);
        verify(authenticationManager).authenticate(authentication);
        verify(request).getMethod();
        verify(request).getServletPath();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void invalidServerRequestsAreReported() throws Exception {
        when(request.getHeader(HeaderAuthenticator.ORIGIN)).thenReturn("server");
        when(request.getHeader(HeaderAuthenticator.SSL_CLIENT_KEY)).thenReturn("$$$$$");

        uut.doFilterInternal(request, response, filterChain);

        verify(request).getHeader(HeaderAuthenticator.ORIGIN);
        verify(request).getHeader(HeaderAuthenticator.SSL_CLIENT_KEY);
        verify(response).setStatus(Response.Status.FORBIDDEN.getStatusCode());
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

        Validation<StatusCode, Principals> p = HeaderAuthenticator.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals(testInput[0], p.success().getUsername());
        assertEquals(uid, p.success().getUid());
        assertEquals(testInput[2], p.success().getDeviceName());
        assertEquals(did, p.success().getDid());

    }

    @Test
    public void testEmptyName() {
        String input = "/CN=$1$$1";

        Validation<StatusCode, Principals> p = HeaderAuthenticator.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals("", p.success().getUsername());
        assertEquals("", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(1, p.success().getDid());
    }

    @Test
    public void testEmptyNameNoSlashes() {
        String input = "CN=$1$$1";

        Validation<StatusCode, Principals> p = HeaderAuthenticator.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals("", p.success().getUsername());
        assertEquals("", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(1, p.success().getDid());
    }

    @Test
    public void testNameWithSpacesAndSpecialCharacters() {
        String input = "CN=John Doe$1$my-test_device$1";

        Validation<StatusCode, Principals> p = HeaderAuthenticator.parseSubjectName(input);

        assertTrue(p.isSuccess());
        assertEquals("John Doe", p.success().getUsername());
        assertEquals("my-test_device", p.success().getDeviceName());
        assertEquals(1, p.success().getUid());
        assertEquals(1, p.success().getDid());
    }

    @Test
    public void testMalformed() {
        String input = "/CN=$1$1";

        Validation<StatusCode, Principals> result = HeaderAuthenticator.parseSubjectName(input);

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    @Test
    public void tooManyDollars() {
        String input = "/CN=omg$4$device$5$tooMuch";

        assertThrows(SecurityException.class, () -> HeaderAuthenticator.parseSubjectName(input));
    }

    @Test
    public void testParseNameWithDollar() {
        String input = "CN=my_user$name$3$my_device_name$6";

        assertThrows(SecurityException.class, () -> HeaderAuthenticator.parseSubjectName(input));
    }

    @Test
    public void testTooFewDollars() {
        Validation<StatusCode, Principals> result = HeaderAuthenticator.parseSubjectName("CN=username$devicename$4");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    @Test
    public void testCompleteGarbage() {
        Validation<StatusCode, Principals> result = HeaderAuthenticator.parseSubjectName("29A");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

    @Test
    public void testEmptySubject() {
        Validation<StatusCode, Principals> result = HeaderAuthenticator.parseSubjectName("");

        assertTrue(result.isFail());
        assertEquals(StatusCode.INVALID_ARGUMENT, result.fail());
    }

}

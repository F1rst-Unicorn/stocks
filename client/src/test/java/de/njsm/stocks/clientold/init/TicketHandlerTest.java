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

package de.njsm.stocks.clientold.init;

import de.njsm.stocks.clientold.exceptions.CryptoException;
import de.njsm.stocks.clientold.exceptions.InitialisationException;
import de.njsm.stocks.clientold.network.TcpHost;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TicketHandlerTest {

    private TicketHandler uut;
    private KeystoreHandler keystoreMock;
    private NetworkHandler networkMock;

    private String user = "some user name";
    private String device = "some device name";
    private int uid = 50;
    private int did = 42;

    @Before
    public void setup() {
        keystoreMock = Mockito.mock(KeystoreHandler.class);
        networkMock = Mockito.mock(NetworkHandler.class);
        uut = new TicketHandler(keystoreMock, networkMock);
    }

    @Test
    public void testMatchingEqualFingerprints() throws InitialisationException, CryptoException {
        String fpr = "matchingFingerprintReturnedfromBoth";
        String caPem = "caPemFileString";
        String chainPem = "chainPemFileString";
        String ca = "ca";
        String chain = "chain";
        TcpHost dummy = new TcpHost("host", 22);
        when(keystoreMock.getFingerPrintFromPem(caPem)).thenReturn(fpr);
        when(networkMock.downloadDocument(dummy, ca)).thenReturn(caPem);
        when(networkMock.downloadDocument(dummy, chain)).thenReturn(chainPem);

        uut.verifyServerCa(dummy, fpr);
        verify(keystoreMock).getFingerPrintFromPem(caPem);
        verify(keystoreMock).importCaCertificate(caPem);
        verify(keystoreMock).importIntermediateCertificate(chainPem);
        verify(networkMock).downloadDocument(dummy, ca);
        verify(networkMock).downloadDocument(dummy, chain);

        verifyNoMoreInteractions(keystoreMock);
        verifyNoMoreInteractions(networkMock);
    }

    @Test(expected = InitialisationException.class)
    public void testMatchingUnequalFingerprints() throws InitialisationException, CryptoException {
        String fprAsArg = "fprAsArg";
        String fprFromCa = "nonMatchingFprFromCa";
        String caPem = "caPemFileString";
        String chainPem = "chainPemFileString";
        String ca = "ca";
        String chain = "chain";
        TcpHost dummy = new TcpHost("host", 22);
        when(keystoreMock.getFingerPrintFromPem(caPem)).thenReturn(fprFromCa);
        when(networkMock.downloadDocument(dummy, ca)).thenReturn(caPem);
        when(networkMock.downloadDocument(dummy, chain)).thenReturn(chainPem);

        uut.verifyServerCa(dummy, fprAsArg);
        verify(keystoreMock).getFingerPrintFromPem(caPem);
        verify(networkMock).downloadDocument(dummy, ca);
        verify(networkMock).downloadDocument(dummy, chain);

        verifyNoMoreInteractions(keystoreMock);
        verifyNoMoreInteractions(networkMock);
    }

    @Test
    public void testGenerateKeyCallForwarding() throws CryptoException {

        uut.generateKey();

        verify(keystoreMock).generateNewKey();
        verifyNoMoreInteractions(keystoreMock);
        verifyNoMoreInteractions(networkMock);
    }

    @Test
    public void testGenerateCsrForwarding() throws CryptoException {
        String expected = user + "$" + uid + "$" + device + "$" + did;

        uut.generateCsr(user, device, uid, did);

        verify(keystoreMock).generateCsr(expected);
        verifyNoMoreInteractions(keystoreMock);
        verifyNoMoreInteractions(networkMock);
    }

    @Test
    public void testSubjectNameGeneration() {

        String expected = user + "$" + uid + "$" + device + "$" + did;

        assertEquals(expected, uut.generateSubjectName(user, device, uid, did));
        verifyNoMoreInteractions(networkMock);
    }
}

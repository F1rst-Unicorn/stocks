/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.PrincipalsHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import fj.data.Validation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CaConsistencyCheckJobTest {

    private CaConsistencyCheckJob uut;

    private AuthAdmin authAdmin;

    private PrincipalsHandler dbHandler;

    private UserDeviceHandler deviceHandler;

    @Before
    public void setup() {
        authAdmin = Mockito.mock(AuthAdmin.class);
        dbHandler = Mockito.mock(PrincipalsHandler.class);
        deviceHandler = Mockito.mock(UserDeviceHandler.class);
        uut = new CaConsistencyCheckJob(authAdmin, dbHandler, deviceHandler);
    }

    @After
    public void tearDown() {
        Mockito.verifyNoMoreInteractions(authAdmin);
        Mockito.verifyNoMoreInteractions(dbHandler);
        Mockito.verifyNoMoreInteractions(deviceHandler);
    }

    @Test
    public void failingDbDoesNothing() {
        when(dbHandler.getPrincipals()).thenReturn(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        uut.run();

        verify(dbHandler).getPrincipals();
        verify(dbHandler).rollback();
    }

    @Test
    public void failingCaDoesNothing() {
        when(dbHandler.getPrincipals()).thenReturn(Validation.success(Collections.emptySet()));
        when(authAdmin.getValidPrincipals()).thenReturn(Validation.fail(StatusCode.DATABASE_UNREACHABLE));

        uut.run();

        verify(dbHandler).getPrincipals();
        verify(dbHandler).rollback();
        verify(authAdmin).getValidPrincipals();
    }

    @Test
    public void happyPathPerformsCleanup() {
        Set<Principals> dbPrincipals = new HashSet<>();
        Set<Principals> caPrincipals = new HashSet<>();

        Principals dbOnlyFailing = new Principals("one", "one", 1, 1);
        Principals dbOnlySuccessful = new Principals("two", "two", 2, 2);
        Principals firstPreserved = new Principals("three", "three", 3, 3);
        Principals secondPreserved = new Principals("four", "four", 4, 4);
        Principals caOnlySuccessful = new Principals("five", "five", 5, 5);
        Principals caOnlyFailing = new Principals("six", "six", 6, 6);

        dbPrincipals.add(dbOnlyFailing);
        dbPrincipals.add(dbOnlySuccessful);
        dbPrincipals.add(firstPreserved);
        dbPrincipals.add(secondPreserved);

        caPrincipals.add(firstPreserved);
        caPrincipals.add(secondPreserved);
        caPrincipals.add(caOnlySuccessful);
        caPrincipals.add(caOnlyFailing);

        when(dbHandler.getPrincipals()).thenReturn(Validation.success(dbPrincipals));
        when(authAdmin.getValidPrincipals()).thenReturn(Validation.success(caPrincipals));
        when(authAdmin.revokeCertificate(caOnlySuccessful.getDid())).thenReturn(StatusCode.SUCCESS);
        when(authAdmin.revokeCertificate(caOnlyFailing.getDid())).thenReturn(StatusCode.CA_UNREACHABLE);
        when(deviceHandler.delete(dbOnlyFailing.toDevice())).thenReturn(StatusCode.DATABASE_UNREACHABLE);
        when(deviceHandler.delete(dbOnlySuccessful.toDevice())).thenReturn(StatusCode.SUCCESS);

        uut.run();

        verify(dbHandler).getPrincipals();
        verify(authAdmin).getValidPrincipals();
        verify(deviceHandler).delete(dbOnlyFailing.toDevice());
        verify(deviceHandler).delete(dbOnlySuccessful.toDevice());
        verify(authAdmin).revokeCertificate(caOnlySuccessful.getDid());
        verify(authAdmin).revokeCertificate(caOnlyFailing.getDid());
        verify(dbHandler).commit();
    }
}

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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Unit;
import de.njsm.stocks.common.api.BitemporalUnit;
import de.njsm.stocks.common.api.UnitForDeletion;
import de.njsm.stocks.common.api.UnitForInsertion;
import de.njsm.stocks.common.api.UnitForRenaming;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesInsertable;
import static de.njsm.stocks.server.v2.matchers.Matchers.matchesVersionable;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

public class UnitHandlerTest extends DbTestCase {

    private UnitHandler uut;

    @Before
    public void setup() {
        uut = new UnitHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void insertingWorks() {
        UnitForInsertion data = new UnitForInsertion("name", "abbreviation");

        Validation<StatusCode, Integer> result = uut.addReturningId(data);

        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(3), result.success());
        Validation<StatusCode, Stream<Unit>> units = uut.get(false, Instant.EPOCH);
        List<Unit> list = units.success().collect(Collectors.toList());
        assertTrue(units.isSuccess());
        assertEquals(3, list.size());
        assertThat(list, hasItem(matchesInsertable(data)));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<Unit>> result = uut.get(true, Instant.EPOCH);

        BitemporalUnit sample = (BitemporalUnit) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void renamingWorks() {
        UnitForRenaming data = new UnitForRenaming(1, 0, "name", "abbreviation");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Unit>> dbData = uut.get(false, Instant.EPOCH);
        List<Unit> records = dbData.success().collect(Collectors.toList());
        assertThat(records, hasItem(matchesVersionable(data)));
    }

    @Test
    public void renamingOnlyNameWorks() {
        UnitForRenaming data = new UnitForRenaming(1, 0, "Default", "new abbreviation");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Unit>> dbData = uut.get(false, Instant.EPOCH);
        List<Unit> records = dbData.success().collect(Collectors.toList());
        assertThat(records, hasItem(matchesVersionable(data)));
    }

    @Test
    public void renamingOnlyAbbreviationWorks() {
        UnitForRenaming data = new UnitForRenaming(1, 0, "new name", "default");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.SUCCESS, result);

        Validation<StatusCode, Stream<Unit>> dbData = uut.get(false, Instant.EPOCH);
        List<Unit> records = dbData.success().collect(Collectors.toList());
        assertThat(records, hasItem(matchesVersionable(data)));
    }

    @Test
    public void missingWhileRenamingIsReported() {
        UnitForRenaming data = new UnitForRenaming(999, 0, "name", "abbreviation");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void invalidWhileRenamingIsReported() {
        UnitForRenaming data = new UnitForRenaming(1, 1, "name", "abbreviation");

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<Unit>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalUnit> data = result.success()
                .map(v -> (BitemporalUnit) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.getName().equals("Default") &&
                        l.getAbbreviation().equals("default") &&
                        l.getInitiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<Unit>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<Unit> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.getName().equals("Default") &&
                        l.getAbbreviation().equals("default")));
    }

    @Test
    public void deletingWithForeignReferencesIsRejected() {
        StatusCode result = uut.delete(new UnitForDeletion(1, 0))
                .bind(uut::commit);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }
}

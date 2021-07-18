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

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.*;
import fj.data.Validation;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
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

public class ScaledUnitHandlerTest extends DbTestCase {

    private ScaledUnitHandler uut;

    @Before
    public void setup() {
        uut = new ScaledUnitHandler(getConnectionFactory(),
                getNewResourceIdentifier(),
                CIRCUIT_BREAKER_TIMEOUT);
        uut.setPrincipals(TEST_USER);
    }

    @Test
    public void insertingWorks() {
        ScaledUnitForInsertion data = new ScaledUnitForInsertion(BigDecimal.ONE, 1);

        Validation<StatusCode, Integer> result = uut.addReturningId(data);

        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(3), result.success());
        Validation<StatusCode, Stream<ScaledUnit>> units = uut.get(false, Instant.EPOCH);
        List<ScaledUnit> list = units.success().collect(Collectors.toList());
        assertTrue(units.isSuccess());
        assertEquals(3, list.size());
        assertThat(list, hasItem(matchesInsertable(data)));
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(true, Instant.EPOCH);

        BitemporalScaledUnit sample = (BitemporalScaledUnit) result.success().findAny().get();
        assertNotNull(sample.getValidTimeStart());
        assertNotNull(sample.getValidTimeEnd());
        assertNotNull(sample.getTransactionTimeStart());
        assertNotNull(sample.getTransactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalScaledUnit> data = result.success()
                .map(v -> (BitemporalScaledUnit) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                        l.getId() == 2 &&
                        l.getVersion() == 0 &&
                        l.getScale().equals(new BigDecimal(3)) &&
                        l.getUnit() == 2 &&
                        l.getInitiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<ScaledUnit> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.getId() == 2 &&
                l.getVersion() == 0 &&
                l.getScale().equals(new BigDecimal(3)) &&
                l.getUnit() == 2));
    }

    @Test
    public void deletingWithForeignReferencesIsRejected() {
        StatusCode result = uut.delete(new ScaledUnitForDeletion(1, 0))
                .bind(uut::commit);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }

    @Test
    public void editingScaleWorks() {
        ScaledUnitForEditing input = new ScaledUnitForEditing(1, 0, new BigDecimal(2), 1);

        StatusCode result = uut.edit(input);

        assertEquals(StatusCode.SUCCESS, result);
        List<ScaledUnit> data = uut.get(false, Instant.EPOCH).success().collect(Collectors.toList());
        assertThat(data, hasItem(matchesVersionable(input)));
    }

    @Test
    public void editingUnitWorks() {
        ScaledUnitForEditing input = new ScaledUnitForEditing(1, 0, new BigDecimal(1), 2);

        StatusCode result = uut.edit(input);

        assertEquals(StatusCode.SUCCESS, result);
        List<ScaledUnit> data = uut.get(false, Instant.EPOCH).success().collect(Collectors.toList());
        assertThat(data, hasItem(matchesVersionable(input)));
    }
}

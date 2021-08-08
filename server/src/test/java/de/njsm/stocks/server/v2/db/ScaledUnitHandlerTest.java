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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.v2.db.jooq.tables.records.ScaledUnitRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesVersionable;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class ScaledUnitHandlerTest extends DbTestCase implements EntityDbTestCase<ScaledUnitRecord, ScaledUnit> {

    private ScaledUnitHandler uut;

    @BeforeEach
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

        assertInsertableIsInserted(result, data, 3, 3);
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(true, Instant.EPOCH);

        BitemporalScaledUnit sample = (BitemporalScaledUnit) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(true, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<BitemporalScaledUnit> data = result.success()
                .map(v -> (BitemporalScaledUnit) v).collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                        l.id() == 2 &&
                        l.version() == 0 &&
                        l.scale().equals(new BigDecimal(3)) &&
                        l.unit() == 2 &&
                        l.initiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(false, Instant.EPOCH);

        assertTrue(result.isSuccess());
        List<ScaledUnit> data = result.success().collect(Collectors.toList());

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 2 &&
                l.version() == 0 &&
                l.scale().equals(new BigDecimal(3)) &&
                l.unit() == 2));
    }

    @Test
    public void deletingWithForeignReferencesIsRejected() {
        StatusCode result = uut.delete(new ScaledUnitForDeletion(1, 0))
                .bind(uut::commit);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }

    @Test
    public void editingMissingScaleIsReported() {
        ScaledUnitForEditing input = new ScaledUnitForEditing(3, 0, new BigDecimal(2), 1);

        StatusCode result = uut.edit(input);

        assertEquals(StatusCode.NOT_FOUND, result);
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

    @Override
    public CrudDatabaseHandler<ScaledUnitRecord, ScaledUnit> getDbHandler() {
        return uut;
    }
}

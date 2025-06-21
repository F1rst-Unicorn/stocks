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

import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.junit.jupiter.api.Assertions.*;

public class ScaledUnitHandlerTest extends DbTestCase implements CrudOperationsTest<ScaledUnitRecord, ScaledUnit> {

    private ScaledUnitHandler uut;

    @BeforeEach
    public void setup() {
        uut = new ScaledUnitHandler(getConnectionFactory());
        uut.setPrincipals(TEST_USER);
    }

    @Override
    public ScaledUnitForInsertion getInsertable() {
        return ScaledUnitForInsertion.builder()
                .scale(BigDecimal.ONE)
                .unit(1)
                .build();
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(Instant.EPOCH);

        BitemporalScaledUnit sample = (BitemporalScaledUnit) result.success().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, Stream<ScaledUnit>> result = uut.get(Instant.EPOCH);

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
    void countingWorks() {
        Validation<StatusCode, Integer> result = uut.countCurrent();

        assertTrue(result.isSuccess());
        assertEquals(3, result.success());
    }

    @Test
    public void deletingWithForeignReferencesIsRejected() {
        StatusCode result = uut.delete(ScaledUnitForDeletion.builder()
                        .id(1)
                        .version(0)
                        .build())
                .bind(uut::commit);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }

    @Test
    public void editingMissingScaleIsReported() {
        ScaledUnitForEditing input = ScaledUnitForEditing.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .scale(BigDecimal.valueOf(2))
                .unit(1)
                .build();

        StatusCode result = uut.edit(input);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void editingScaleWorks() {
        ScaledUnitForEditing input = ScaledUnitForEditing.builder()
                .id(1)
                .version(0)
                .scale(BigDecimal.valueOf(2))
                .unit(1)
                .build();

        StatusCode result = uut.edit(input);

        assertEditingWorked(input, result);
    }

    @Test
    public void editingUnitWorks() {
        ScaledUnitForEditing input = ScaledUnitForEditing.builder()
                .id(1)
                .version(0)
                .scale(BigDecimal.valueOf(1))
                .unit(2)
                .build();

        StatusCode result = uut.edit(input);

        assertEditingWorked(input, result);
    }

    @Override
    public CrudDatabaseHandler<ScaledUnitRecord, ScaledUnit> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 3;
    }

    @Override
    public ScaledUnitForDeletion getUnknownEntity() {
        return ScaledUnitForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public ScaledUnitForDeletion getWrongVersionEntity() {
        return ScaledUnitForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public ScaledUnitForDeletion getValidEntity() {
        return ScaledUnitForDeletion.builder()
                .id(3)
                .version(0)
                .build();
    }
}

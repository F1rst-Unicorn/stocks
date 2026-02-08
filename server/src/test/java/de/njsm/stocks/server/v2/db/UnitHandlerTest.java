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
import de.njsm.stocks.server.v2.db.jooq.tables.records.UnitRecord;
import fj.data.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.server.v2.db.CrudDatabaseHandler.INFINITY;
import static org.junit.jupiter.api.Assertions.*;

public class UnitHandlerTest extends DbTestCase implements CrudOperationsTest<UnitRecord, Unit> {

    private UnitHandler uut;

    @BeforeEach
    public void setup() {
        uut = new UnitHandler(getConnectionFactory());
    }

    @Override
    public UnitForInsertion getInsertable() {
        return UnitForInsertion.builder()
                .name("name")
                .abbreviation("abbreviation")
                .build();
    }

    @Test
    public void bitemporalDataIsPresentWhenDesired() {

        Validation<StatusCode, List<Unit>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        BitemporalUnit sample = (BitemporalUnit) result.success().stream().findAny().get();
        assertNotNull(sample.validTimeStart());
        assertNotNull(sample.validTimeEnd());
        assertNotNull(sample.transactionTimeStart());
        assertNotNull(sample.transactionTimeEnd());
    }

    @Test
    public void renamingWorks() {
        UnitForRenaming data = UnitForRenaming.builder()
                .id(1)
                .version(0)
                .name("name")
                .abbreviation("abbreviation")
                .build();

        StatusCode result = uut.rename(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void renamingOnlyNameWorks() {
        UnitForRenaming data = UnitForRenaming.builder()
                .id(1)
                .version(0)
                .name("Default")
                .abbreviation("new abbreviation")
                .build();

        StatusCode result = uut.rename(data);

        assertEditingWorked(data, result);
    }

    @Test
    void gettingDataUpUntilWorks() {
        var updateBackend = new UpdateBackend(getConnectionFactory());
        var unitTimestamp = updateBackend.get().success()
                .stream()
                .filter(v -> v.table().equals("unit"))
                .findFirst()
                .orElseThrow();

        var dataBeforeUpdate = uut.get(Instant.EPOCH, unitTimestamp.lastUpdate());
        assertTrue(dataBeforeUpdate.isSuccess());
        var dataBefore = dataBeforeUpdate.success();
        assertEquals(3, dataBefore.size());

        uut.rename(UnitForRenaming.builder().id(1).version(0).name("new name").abbreviation("new").build());
        var unitTimestampAfterUpdate = updateBackend.get().success()
                .stream()
                .filter(v -> v.table().equals("unit"))
                .findFirst()
                .orElseThrow();
        assertTrue(unitTimestampAfterUpdate.lastUpdate().isAfter(unitTimestamp.lastUpdate()));

        var dataAfterUpdate = uut.get(unitTimestamp.lastUpdate(), unitTimestampAfterUpdate.lastUpdate());
        assertTrue(dataAfterUpdate.isSuccess());
        var dataAfter = dataAfterUpdate.success();
        assertEquals(3, dataAfter.size());
        assertNotEquals(dataBefore, dataAfter);
    }

    @Test
    public void renamingOnlyAbbreviationWorks() {
        UnitForRenaming data = UnitForRenaming.builder()
                .id(1)
                .version(0)
                .name("new name")
                .abbreviation("default")
                .build();

        StatusCode result = uut.rename(data);

        assertEditingWorked(data, result);
    }

    @Test
    public void missingWhileRenamingIsReported() {
        UnitForRenaming data = UnitForRenaming.builder()
                .id(999)
                .version(0)
                .name("name")
                .abbreviation("abbreviation")
                .build();

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.NOT_FOUND, result);
    }

    @Test
    public void invalidWhileRenamingIsReported() {
        UnitForRenaming data = UnitForRenaming.builder()
                .id(1)
                .version(1)
                .name("name")
                .abbreviation("abbreviation")
                .build();

        StatusCode result = uut.rename(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
    }

    @Test
    public void gettingBitemporalWorks() {
        Validation<StatusCode, List<Unit>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        assertTrue(result.isSuccess());
        List<BitemporalUnit> data = result.success()
                .stream()
                .map(v -> (BitemporalUnit) v).toList();

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.name().equals("Default") &&
                        l.abbreviation().equals("default") &&
                        l.initiates() == 1));
    }

    @Test
    public void gettingWorks() {
        Validation<StatusCode, List<Unit>> result = uut.get(Instant.EPOCH, INFINITY.toInstant());

        assertTrue(result.isSuccess());
        List<Unit> data = result.success().stream().toList();

        assertTrue(data.stream().anyMatch(l ->
                l.id() == 1 &&
                        l.version() == 0 &&
                        l.name().equals("Default") &&
                        l.abbreviation().equals("default")));
    }

    @Test
    public void deletingWithForeignReferencesIsRejected() {
        UnitForDeletion data = UnitForDeletion.builder()
                .id(1)
                .version(0)
                .build();

        StatusCode result = uut.delete(data)
                .bind(uut::commit);

        assertEquals(StatusCode.FOREIGN_KEY_CONSTRAINT_VIOLATION, result);
    }

    @Override
    public CrudDatabaseHandler<UnitRecord, Unit> getDbHandler() {
        return uut;
    }

    @Override
    public int getNumberOfEntities() {
        return 3;
    }

    @Override
    public Versionable<Unit> getUnknownEntity() {
        return UnitForDeletion.builder()
                .id(getNumberOfEntities() + 1)
                .version(0)
                .build();
    }

    @Override
    public Versionable<Unit> getWrongVersionEntity() {
        return UnitForDeletion.builder()
                .id(getValidEntity().id())
                .version(getValidEntity().version() + 1)
                .build();
    }

    @Override
    public Versionable<Unit> getValidEntity() {
        return UnitForDeletion.builder()
                .id(3)
                .version(0)
                .build();
    }
}

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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.business.*;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.entities.conflict.LocationEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.ScaledUnitEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.UnitEditConflictData;
import de.njsm.stocks.client.database.*;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.BitemporalOperations.currentUpdate;
import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;


public class ConflictRepositoryImplTest extends DbTestCase {

    private ConflictRepository uut;

    private ErrorRecorder errorRecorder;

    private ErrorRepository errorRepository;

    @Before
    public void setUp() {
        uut = new ConflictRepositoryImpl(stocksDatabase.errorDao());
        errorRecorder = new ErrorRecorderImpl(stocksDatabase.errorDao(), this);
        errorRepository = new ErrorRepositoryImpl(stocksDatabase.errorDao());

        List<UpdateDbEntity> updates = Arrays.stream(EntityType.values())
                .map(v -> UpdateDbEntity.create(v, Instant.EPOCH))
                .collect(Collectors.toList());
        stocksDatabase.synchronisationDao().insert(updates);
    }

    @Test
    public void invalidErrorActionThrowsException() throws InterruptedException {
        errorRecorder.recordSynchronisationError(new SubsystemException("test"));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);

        uut.getLocationEditConflict(error.id()).test().await().assertError(IllegalArgumentException.class);
    }

    @Test
    public void gettingCurrentLocationEditConflictWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        LocationDbEntity original = standardEntities.locationDbEntity();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(original));
        LocationForEditing localEdit = LocationForEditing.builder()
                .id(original.id())
                .version(original.version())
                .name("Fridge")
                .description("The cold one")
                .build();
        LocationToEdit remoteEdit = LocationToEdit.builder()
                .id(original.id())
                .name("remote name")
                .description("remote description")
                .build();
        stocksDatabase.synchronisationDao().writeLocations(currentUpdate(original,
                (BitemporalOperations.EntityEditor<LocationDbEntity, LocationDbEntity.Builder>) builder ->
                        builder.name(remoteEdit.name())
                                .description(remoteEdit.description()),
                editTime));
        setNow(editTime.plusSeconds(1));
        errorRecorder.recordLocationEditError(new SubsystemException("test"), localEdit);
        setArtificialDbNow(editTime.plusSeconds(1));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);

        TestObserver<LocationEditConflictData> observable = test(uut.getLocationEditConflict(error.id()));

        LocationEditConflictData actual = observable.values().get(0);
        assertEquals(error.id(), actual.errorId());
        assertEquals(localEdit.id(), actual.id());
        assertEquals(localEdit.version(), actual.originalVersion());
        assertEquals(localEdit.name(), actual.name().local());
        assertEquals(localEdit.description(), actual.description().local());
        assertEquals(original.name(), actual.name().original());
        assertEquals(original.description(), actual.description().original());
        assertEquals(remoteEdit.name(), actual.name().remote());
        assertEquals(remoteEdit.description(), actual.description().remote());
    }

    @Test
    public void gettingLocationEditConflictOfDeletedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        Instant deleteTime = Instant.EPOCH.plusSeconds(10);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        LocationDbEntity original = standardEntities.locationDbEntity();
        LocationForEditing localEdit = LocationForEditing.builder()
                .id(original.id())
                .version(original.version())
                .name("Fridge")
                .description("The cold one")
                .build();
        LocationToEdit remoteEdit = LocationToEdit.builder()
                .id(original.id())
                .name("remote name")
                .description("remote description")
                .build();
        stocksDatabase.synchronisationDao().writeLocations(singletonList(original));
        stocksDatabase.synchronisationDao().writeLocations(currentUpdate(original,
                (BitemporalOperations.EntityEditor<LocationDbEntity, LocationDbEntity.Builder>) v ->
                        v.name(remoteEdit.name()).description(remoteEdit.description()),
                editTime));
        setNow(editTime.plusSeconds(2));
        errorRecorder.recordLocationEditError(exception, localEdit);
        LocationDbEntity remoteEdited = stocksDatabase.errorDao().getCurrentLocation(original.id());
        stocksDatabase.synchronisationDao().writeLocations(currentDelete(remoteEdited, deleteTime));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);
        setArtificialDbNow(deleteTime.plusSeconds(3));

        TestObserver<LocationEditConflictData> observable = test(uut.getLocationEditConflict(error.id()));

        LocationEditConflictData actual = observable.values().get(0);
        assertEquals(error.id(), actual.errorId());
        assertEquals(localEdit.id(), actual.id());
        assertEquals(localEdit.version(), actual.originalVersion());
        assertEquals(localEdit.name(), actual.name().local());
        assertEquals(localEdit.description(), actual.description().local());
        assertEquals(original.name(), actual.name().original());
        assertEquals(original.description(), actual.description().original());
        assertEquals(remoteEdit.name(), actual.name().remote());
        assertEquals(remoteEdit.description(), actual.description().remote());
    }

    @Test
    public void gettingCurrentUnitEditConflictWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        UnitDbEntity original = standardEntities.unitDbEntity();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(original));
        UnitForEditing localEdit = UnitForEditing.builder()
                .id(original.id())
                .version(original.version())
                .name("Fridge")
                .abbreviation("The cold one")
                .build();
        UnitToEdit remoteEdit = UnitToEdit.builder()
                .id(original.id())
                .name("remote name")
                .abbreviation("remote abbreviation")
                .build();
        stocksDatabase.synchronisationDao().writeUnits(currentUpdate(original,
                (BitemporalOperations.EntityEditor<UnitDbEntity, UnitDbEntity.Builder>) builder ->
                        builder.name(remoteEdit.name())
                                .abbreviation(remoteEdit.abbreviation()),
                editTime));
        setNow(editTime.plusSeconds(1));
        errorRecorder.recordUnitEditError(new SubsystemException("test"), localEdit);
        setArtificialDbNow(editTime.plusSeconds(1));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);

        TestObserver<UnitEditConflictData> observable = test(uut.getUnitEditConflict(error.id()));

        UnitEditConflictData actual = observable.values().get(0);
        assertEquals(error.id(), actual.errorId());
        assertEquals(localEdit.id(), actual.id());
        assertEquals(localEdit.version(), actual.originalVersion());
        assertEquals(localEdit.name(), actual.name().local());
        assertEquals(localEdit.abbreviation(), actual.abbreviation().local());
        assertEquals(original.name(), actual.name().original());
        assertEquals(original.abbreviation(), actual.abbreviation().original());
        assertEquals(remoteEdit.name(), actual.name().remote());
        assertEquals(remoteEdit.abbreviation(), actual.abbreviation().remote());
    }

    @Test
    public void gettingUnitEditConflictOfDeletedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        Instant deleteTime = Instant.EPOCH.plusSeconds(10);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        UnitDbEntity original = standardEntities.unitDbEntity();
        UnitForEditing localEdit = UnitForEditing.builder()
                .id(original.id())
                .version(original.version())
                .name("Fridge")
                .abbreviation("The cold one")
                .build();
        UnitToEdit remoteEdit = UnitToEdit.builder()
                .id(original.id())
                .name("remote name")
                .abbreviation("remote abbreviation")
                .build();
        stocksDatabase.synchronisationDao().writeUnits(singletonList(original));
        stocksDatabase.synchronisationDao().writeUnits(currentUpdate(original,
                (BitemporalOperations.EntityEditor<UnitDbEntity, UnitDbEntity.Builder>) v ->
                        v.name(remoteEdit.name()).abbreviation(remoteEdit.abbreviation()),
                editTime));
        setNow(editTime.plusSeconds(2));
        errorRecorder.recordUnitEditError(exception, localEdit);
        UnitDbEntity remoteEdited = stocksDatabase.errorDao().getCurrentUnit(original.id());
        stocksDatabase.synchronisationDao().writeUnits(currentDelete(remoteEdited, deleteTime));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);
        setArtificialDbNow(deleteTime.plusSeconds(3));

        TestObserver<UnitEditConflictData> observable = test(uut.getUnitEditConflict(error.id()));

        UnitEditConflictData actual = observable.values().get(0);
        assertEquals(error.id(), actual.errorId());
        assertEquals(localEdit.id(), actual.id());
        assertEquals(localEdit.version(), actual.originalVersion());
        assertEquals(localEdit.name(), actual.name().local());
        assertEquals(localEdit.abbreviation(), actual.abbreviation().local());
        assertEquals(original.name(), actual.name().original());
        assertEquals(original.abbreviation(), actual.abbreviation().original());
        assertEquals(remoteEdit.name(), actual.name().remote());
        assertEquals(remoteEdit.abbreviation(), actual.abbreviation().remote());
    }

    @Test
    public void gettingCurrentScaledUnitEditConflictWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        UnitDbEntity localUnit = standardEntities.unitDbEntity();
        UnitDbEntity remoteUnit = standardEntities.unitDbEntityBuilder()
                .id(randomnessProvider.getId("remote unit id"))
                .name("remote")
                .abbreviation("remote")
                .build();
        stocksDatabase.synchronisationDao().writeUnits(asList(localUnit, remoteUnit));
        ScaledUnitDbEntity original = standardEntities.scaledUnitDbEntityBuilder()
                .unit(localUnit.id())
                .build();
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(original));
        ScaledUnitForEditing localEdit = ScaledUnitForEditing.create(
                original.id(),
                original.version(),
                BigDecimal.valueOf(4),
                localUnit.id());
        ScaledUnitToEdit remoteEdit = ScaledUnitToEdit.create(
                original.id(),
                BigDecimal.valueOf(5),
                localUnit.id());
        stocksDatabase.synchronisationDao().writeScaledUnits(currentUpdate(original,
                (BitemporalOperations.EntityEditor<ScaledUnitDbEntity, ScaledUnitDbEntity.Builder>) builder ->
                        builder.scale(remoteEdit.scale())
                                .unit(remoteEdit.unit()),
                editTime));
        setNow(editTime.plusSeconds(1));
        errorRecorder.recordScaledUnitEditError(new SubsystemException("test"), localEdit);
        setArtificialDbNow(editTime.plusSeconds(1));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);

        TestObserver<ScaledUnitEditConflictData> observable = test(uut.getScaledUnitEditConflict(error.id()));

        ScaledUnitEditConflictData actual = observable.values().get(0);
        assertEquals(error.id(), actual.errorId());
        assertEquals(localEdit.id(), actual.id());
        assertEquals(localEdit.version(), actual.originalVersion());
        assertEquals(localEdit.scale(), actual.scale().local());
        assertEquals(localEdit.unit(), actual.unit().local().id());
        assertEquals(original.scale(), actual.scale().original());
        assertEquals(original.unit(), actual.unit().original().id());
        assertEquals(remoteEdit.scale(), actual.scale().remote());
        assertEquals(remoteEdit.unit(), actual.unit().remote().id());
    }

    @Test
    public void gettingScaledUnitEditConflictOfDeletedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        Instant deleteTime = Instant.EPOCH.plusSeconds(10);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        UnitDbEntity localUnit = standardEntities.unitDbEntity();
        UnitDbEntity remoteUnit = standardEntities.unitDbEntityBuilder()
                .id(randomnessProvider.getId("remote unit id"))
                .name("remote")
                .abbreviation("remote")
                .build();
        stocksDatabase.synchronisationDao().writeUnits(asList(localUnit, remoteUnit));
        ScaledUnitDbEntity original = standardEntities.scaledUnitDbEntityBuilder()
                .unit(localUnit.id())
                .build();
        ScaledUnitForEditing localEdit = ScaledUnitForEditing.create(
                original.id(),
                original.version(),
                BigDecimal.valueOf(4),
                localUnit.id());
        ScaledUnitToEdit remoteEdit = ScaledUnitToEdit.create(
                original.id(),
                BigDecimal.valueOf(5),
                localUnit.id());
        stocksDatabase.synchronisationDao().writeScaledUnits(singletonList(original));
        stocksDatabase.synchronisationDao().writeScaledUnits(currentUpdate(original,
                (BitemporalOperations.EntityEditor<ScaledUnitDbEntity, ScaledUnitDbEntity.Builder>) v ->
                        v.scale(remoteEdit.scale())
                                .unit(remoteEdit.unit()),
                editTime));
        setNow(editTime.plusSeconds(2));
        errorRecorder.recordScaledUnitEditError(exception, localEdit);
        ScaledUnitDbEntity remoteEdited = stocksDatabase.errorDao().getCurrentScaledUnit(original.id());
        stocksDatabase.synchronisationDao().writeScaledUnits(currentDelete(remoteEdited, deleteTime));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);
        setArtificialDbNow(deleteTime.plusSeconds(3));

        TestObserver<ScaledUnitEditConflictData> observable = test(uut.getScaledUnitEditConflict(error.id()));

        ScaledUnitEditConflictData actual = observable.values().get(0);
        assertEquals(error.id(), actual.errorId());
        assertEquals(localEdit.id(), actual.id());
        assertEquals(localEdit.version(), actual.originalVersion());
        assertEquals(localEdit.scale(), actual.scale().local());
        assertEquals(localEdit.unit(), actual.unit().local().id());
        assertEquals(original.scale(), actual.scale().original());
        assertEquals(original.unit(), actual.unit().original().id());
        assertEquals(remoteEdit.scale(), actual.scale().remote());
        assertEquals(remoteEdit.unit(), actual.unit().remote().id());
    }
}

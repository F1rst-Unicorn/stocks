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

import de.njsm.stocks.client.business.ConflictRepository;
import de.njsm.stocks.client.business.ErrorRecorder;
import de.njsm.stocks.client.business.ErrorRepository;
import de.njsm.stocks.client.business.SubsystemException;
import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.business.entities.conflict.LocationEditConflictData;
import de.njsm.stocks.client.business.entities.conflict.UnitEditConflictData;
import de.njsm.stocks.client.database.DbTestCase;
import de.njsm.stocks.client.database.LocationDbEntity;
import de.njsm.stocks.client.database.StandardEntities;
import de.njsm.stocks.client.database.UnitDbEntity;
import io.reactivex.rxjava3.observers.TestObserver;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;

import static de.njsm.stocks.client.database.Util.test;
import static de.njsm.stocks.client.database.Util.testList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;


public class ConflictRepositoryImplTest extends DbTestCase {

    private ConflictRepository uut;

    private ErrorRecorder errorRecorder;

    private ErrorRepository errorRepository;

    @Before
    public void setUp() {
        uut = new ConflictRepositoryImpl(stocksDatabase.errorDao());
        errorRecorder = new ErrorRecorderImpl(stocksDatabase.errorDao());
        errorRepository = new ErrorRepositoryImpl(stocksDatabase.errorDao());
    }

    @Test
    public void invalidErrorActionThrowsException() throws InterruptedException {
        errorRecorder.recordSynchronisationError(new SubsystemException("test"));
        ErrorDescription error = testList(errorRepository.getErrors()).values().get(0).get(0);

        uut.getLocationEditConflict(error.id()).test().await().assertError(IllegalArgumentException.class);
    }

    @Test
    public void gettingLocationEditConflictWorks() {
        Instant editTime = Instant.now();
        LocationDbEntity original = StandardEntities.locationDbEntity();
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
        stocksDatabase.synchronisationDao().writeLocations(StandardEntities.bitemporalEdit(original,
                (StandardEntities.EntityEditor<LocationDbEntity, LocationDbEntity.Builder>) builder ->
                        builder.name(remoteEdit.name())
                                .description(remoteEdit.description()),
                editTime));
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
    public void gettingUnitEditConflictWorks() {
        Instant editTime = Instant.now();
        UnitDbEntity original = StandardEntities.unitDbEntity();
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
        stocksDatabase.synchronisationDao().writeUnits(StandardEntities.bitemporalEdit(original,
                (StandardEntities.EntityEditor<UnitDbEntity, UnitDbEntity.Builder>) builder ->
                        builder.name(remoteEdit.name())
                                .abbreviation(remoteEdit.abbreviation()),
                editTime));
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
}

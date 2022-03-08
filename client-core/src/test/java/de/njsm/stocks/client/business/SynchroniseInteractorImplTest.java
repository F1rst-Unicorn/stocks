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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.EntityType;
import de.njsm.stocks.client.business.entities.LocationForSynchronisation;
import de.njsm.stocks.client.business.entities.StatusCode;
import de.njsm.stocks.client.business.entities.Update;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SynchroniseInteractorImplTest {

    @Mock
    UpdateService updateService;

    @Mock
    SynchronisationRepository synchronisationRepository;

    @Mock
    ErrorRecorder errorRecorder;

    private SynchroniseInteractor uut;

    @BeforeEach
    void setUp() {
        uut = new SynchroniseInteractorImpl(updateService, synchronisationRepository, errorRecorder);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(synchronisationRepository);
        verifyNoMoreInteractions(updateService);
        verifyNoMoreInteractions(errorRecorder);
    }

    @Test
    void doesntSynchroniseIfNoServerChanges() {
        List<Update> commonlyAgreedState = singletonList(Update.create(EntityType.LOCATION, Instant.EPOCH));
        when(updateService.getUpdates()).thenReturn(commonlyAgreedState);
        when(synchronisationRepository.getUpdates()).thenReturn(commonlyAgreedState);

        uut.synchronise();

        verify(updateService).getUpdates();
        verify(synchronisationRepository).getUpdates();
        verify(synchronisationRepository).writeUpdates(commonlyAgreedState);
    }

    @Test
    void synchronisesIfServerHasMoreRecentData() {
        Update localUpdate = Update.create(EntityType.LOCATION, Instant.EPOCH);
        Update serverUpdate = Update.create(localUpdate.table(), localUpdate.lastUpdate().plusSeconds(1));
        when(updateService.getUpdates()).thenReturn(singletonList(serverUpdate));
        when(synchronisationRepository.getUpdates()).thenReturn(singletonList(localUpdate));
        List<LocationForSynchronisation> locations = singletonList(LocationForSynchronisation.builder()
                .id(1)
                .version(2)
                .validTimeStart(Instant.EPOCH)
                .validTimeEnd(Constants.INFINITY)
                .transactionTimeStart(Instant.EPOCH)
                .transactionTimeEnd(Constants.INFINITY)
                .initiates(3)
                .name("name")
                .description("description")
                .build());
        when(updateService.getLocations(localUpdate.lastUpdate())).thenReturn(locations);

        uut.synchronise();

        verify(updateService).getUpdates();
        verify(synchronisationRepository).getUpdates();
        verify(updateService).getLocations(localUpdate.lastUpdate());
        verify(synchronisationRepository).writeLocations(locations);
        verify(synchronisationRepository).writeUpdates(singletonList(serverUpdate));
    }

    @Test
    void initialisesDataIfNoLocalUpdatePresent() {
        Update serverUpdate = Update.create(EntityType.LOCATION, Instant.EPOCH);
        when(updateService.getUpdates()).thenReturn(singletonList(serverUpdate));
        when(synchronisationRepository.getUpdates()).thenReturn(emptyList());
        List<LocationForSynchronisation> locations = singletonList(LocationForSynchronisation.builder()
                .id(1)
                .version(2)
                .validTimeStart(Instant.EPOCH)
                .validTimeEnd(Constants.INFINITY)
                .transactionTimeStart(Instant.EPOCH)
                .transactionTimeEnd(Constants.INFINITY)
                .initiates(3)
                .name("name")
                .description("description")
                .build());
        when(updateService.getLocations(Instant.MIN)).thenReturn(locations);

        uut.synchronise();

        verify(updateService).getUpdates();
        verify(synchronisationRepository).getUpdates();
        verify(updateService).getLocations(Instant.MIN);
        verify(synchronisationRepository).initialiseLocations(locations);
        verify(synchronisationRepository).writeUpdates(singletonList(serverUpdate));
    }

    @Test
    void exceptionDuringGettingUpdatesIsRecorded() {
        StatusCodeException expected = new StatusCodeException(StatusCode.DATABASE_UNREACHABLE);
        when(updateService.getUpdates()).thenThrow(expected);

        uut.synchronise();

        verify(errorRecorder).recordError(ErrorRecorder.Action.SYNCHRONISATION, expected);
        verify(updateService).getUpdates();
    }
}

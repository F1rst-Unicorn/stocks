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

package de.njsm.stocks.client.business.event;

import de.njsm.stocks.client.business.Localiser;
import de.njsm.stocks.client.business.entities.event.EditedField;
import de.njsm.stocks.client.business.entities.event.LocationCreatedEvent;
import de.njsm.stocks.client.business.entities.event.LocationDeletedEvent;
import de.njsm.stocks.client.business.entities.event.LocationEditedEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static io.reactivex.rxjava3.core.Single.just;
import static java.util.List.of;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EventInteractorImplTest {

    private EventInteractorImpl uut;

    @Mock
    private EventRepository repository;

    private Localiser localiser;

    @BeforeEach
    void setUp() {
        localiser = new Localiser(null);
        uut = new EventInteractorImpl(repository, new ActivityEventFactory(localiser), localiser);
    }

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository);
    }

    @Test
    void singleLocationCreatedIsTransformed() {
        LocationEventFeedItem input = getValid();
        when(repository.getLocationFeed(localiser.toInstant(LocalDate.EPOCH))).thenReturn(just(of(input)));

        var actual = uut.getEventsOf(LocalDate.EPOCH);

        actual.test().assertValue(of(LocationCreatedEvent.create(
                input.id(),
                localiser.toLocalDateTime(Instant.EPOCH),
                input.userName(),
                input.name()
        )));
    }

    @Test
    void singleLocationDeletedIsTransformed() {
        LocationEventFeedItem input = getTerminated("Fridge", "");
        when(repository.getLocationFeed(localiser.toInstant(LocalDate.EPOCH))).thenReturn(just(of(input)));

        var actual = uut.getEventsOf(LocalDate.EPOCH);

        actual.test().assertValue(of(LocationDeletedEvent.create(
                input.id(),
                localiser.toLocalDateTime(Instant.EPOCH),
                input.userName(),
                input.name()
        )));
    }

    @Test
    void singleLocationUpdatedIsTransformed() {
        LocationEventFeedItem current = getValid();
        LocationEventFeedItem former = getTerminated("Basement", "altered");
        when(repository.getLocationFeed(localiser.toInstant(LocalDate.EPOCH))).thenReturn(just(of(former, current)));

        var actual = uut.getEventsOf(LocalDate.EPOCH);

        actual.test().assertValue(of(LocationEditedEvent.create(
                current.id(),
                localiser.toLocalDateTime(Instant.EPOCH),
                former.userName(),
                EditedField.create(former.name(), current.name()),
                EditedField.create(former.description(), current.description())
        )));
    }

    @Test
    void twoEventsMixedAreSortedOut() {
        LocationEventFeedItem first = LocationEventFeedItem.create(
                1,
                INFINITY,
                Instant.EPOCH,
                "Jack",
                "Fridge",
                "");
        LocationEventFeedItem second = LocationEventFeedItem.create(
                2,
                INFINITY,
                Instant.EPOCH.plusSeconds(1),
                "Jack",
                "Fridge",
                "");
        when(repository.getLocationFeed(localiser.toInstant(LocalDate.EPOCH))).thenReturn(just(of(first, second)));

        var actual = uut.getEventsOf(LocalDate.EPOCH);

        actual.test().assertValue(of(
                LocationCreatedEvent.create(
                        second.id(),
                        localiser.toLocalDateTime(second.transactionTimeStart()),
                        second.userName(),
                        second.name()),
                LocationCreatedEvent.create(
                        first.id(),
                        localiser.toLocalDateTime(first.transactionTimeStart()),
                        first.userName(),
                        first.name())
        ));
    }

    private static LocationEventFeedItem getValid() {
        return LocationEventFeedItem.create(
                1,
                INFINITY,
                Instant.EPOCH,
                "Jack",
                "Fridge",
                "");
    }

    private static LocationEventFeedItem getTerminated(String name, String description) {
        return LocationEventFeedItem.create(
                1,
                Instant.EPOCH,
                Instant.EPOCH,
                "Jack",
                name,
                description);
    }
}

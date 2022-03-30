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

import de.njsm.stocks.client.business.entities.LocationAddForm;
import de.njsm.stocks.client.business.entities.SynchronisationErrorDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ErrorRetryTest {

    private ErrorRetryInteractor uut;

    private LocationAddInteractor locationAddInteractor;

    private Synchroniser synchroniser;

    @BeforeEach
    void setUp() {
        locationAddInteractor = mock(LocationAddInteractor.class);
        synchroniser = mock(Synchroniser.class);
        uut = new ErrorRetryInteractorImpl(locationAddInteractor, synchroniser);
    }

    @Test
    void retryingToAddLocationDispatchesToLocationAdder() {
        LocationAddForm input = LocationAddForm.create("Fridge", "the cold one");

        uut.retry(input);

        verify(locationAddInteractor).addLocation(input);
    }

    @Test
    void retryingSynchronisationDispatches() {
        SynchronisationErrorDetails input = new SynchronisationErrorDetails();

        uut.retry(input);

        verify(synchroniser).synchronise();
    }
}

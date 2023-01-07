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

import de.njsm.stocks.client.business.entities.UnitForListing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UnitListInteractorImplTest {

    @Test
    void gettingUnitsFetchesFromRepository() {
        List<UnitForListing> expected = singletonList(UnitForListing.create(1, "Liter", "l"));
        UnitRepository repository = mock(UnitRepository.class);
        when(repository.getUnits()).thenReturn(Observable.just(expected));
        UnitListInteractor uut = new UnitListInteractorImpl(repository);

        Observable<List<UnitForListing>> actual = uut.getUnits();

        actual.test().assertValue(expected);
    }
}

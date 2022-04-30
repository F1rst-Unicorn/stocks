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

import de.njsm.stocks.client.business.entities.ScaledUnitForListing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScaledUnitListInteractorImplTest {

    @Test
    void gettingScaledUnitsFetchesFromRepository() {
        List<ScaledUnitForListing> expected = singletonList(ScaledUnitForListing.create(1, "g", BigDecimal.TEN));
        ScaledUnitRepository repository = mock(ScaledUnitRepository.class);
        when(repository.getScaledUnits()).thenReturn(Observable.just(expected));
        ScaledUnitListInteractor uut = new ScaledUnitListInteractorImpl(repository);

        Observable<List<ScaledUnitForListing>> actual = uut.getScaledUnits();

        actual.test().assertValue(expected);
    }
}

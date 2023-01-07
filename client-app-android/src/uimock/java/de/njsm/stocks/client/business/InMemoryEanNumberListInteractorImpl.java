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

import de.njsm.stocks.client.business.entities.EanNumberAddForm;
import de.njsm.stocks.client.business.entities.EanNumberForListing;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.testdata.EanNumbersForListing;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

class InMemoryEanNumberListInteractorImpl implements EanNumberListInteractor {

    private final BehaviorSubject<List<EanNumberForListing>> data;

    @Inject
    InMemoryEanNumberListInteractorImpl() {
        this.data = BehaviorSubject.createDefault(EanNumbersForListing.generate());
    }

    @Override
    public Observable<List<EanNumberForListing>> get(Id<Food> user) {
        return data.delay(1, TimeUnit.SECONDS);
    }

    @Override
    public void add(EanNumberAddForm eanNumberAddForm) {

    }
}
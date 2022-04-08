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

import de.njsm.stocks.client.business.entities.ErrorDescription;
import de.njsm.stocks.client.testdata.ErrorDescriptions;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.TimeUnit;

class InMemoryErrorListInteractorImpl implements ErrorListInteractor {

    private final BehaviorSubject<List<ErrorDescription>> data;

    @Inject
    InMemoryErrorListInteractorImpl(ErrorDescriptions locationsForListing) {
        this.data = locationsForListing.getData();
    }

    @Override
    public Observable<List<ErrorDescription>> getErrors() {
        return data.delay(1, TimeUnit.SECONDS);
    }

    @Override
    public Observable<ErrorDescription> getError(long id) {
        return data.map(v -> v.get(0));
    }
}

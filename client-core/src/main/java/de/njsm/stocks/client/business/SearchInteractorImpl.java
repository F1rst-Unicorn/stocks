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

import de.njsm.stocks.client.business.entities.*;
import de.njsm.stocks.client.execution.Scheduler;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

class SearchInteractorImpl implements SearchInteractor {

    private final SearchRepository repository;

    private final FoodRegrouper foodRegrouper;

    private final Scheduler scheduler;

    @Inject
    SearchInteractorImpl(SearchRepository repository, FoodRegrouper foodRegrouper, Scheduler scheduler) {
        this.repository = repository;
        this.foodRegrouper = foodRegrouper;
        this.scheduler = scheduler;
    }

    @Override
    public void storeRecentSearch(String query) {
        scheduler.schedule(Job.create(Job.Type.SAVE_SEARCH, () -> repository.storeRecentSearch(query)));
    }

    @Override
    public void storeFoundFood(Id<Food> food) {
        scheduler.schedule(Job.create(Job.Type.SAVE_SEARCH, () -> repository.storeFoundFood(food)));
    }

    @Override
    public Observable<List<SearchedFoodForListing>> get(String query) {
        Observable<List<StoredFoodAmount>> presentAmounts = repository.getFoodAmountsIn(query);
        Observable<List<StoredFoodAmount>> absentAmounts = repository.getFoodDefaultUnitOfFoodWithoutItems(query);
        Observable<List<StoredFoodAmount>> allAmounts = Observable.combineLatest(presentAmounts, absentAmounts,
                (u, v) -> Stream.concat(u.stream(), v.stream())
                        .collect(toList()));

        return Observable.zip(
                repository.getFoodBy(query),
                allAmounts,
                (v, u) -> foodRegrouper.regroup(v, u, SearchedFoodForListing::create, SearchedFoodForListing::name)
        );
    }
}

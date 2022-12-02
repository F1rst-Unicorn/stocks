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

package de.njsm.stocks.client.database;

import android.database.Cursor;
import de.njsm.stocks.client.business.Clock;
import de.njsm.stocks.client.business.SearchRepository;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.SearchedFoodForListingBaseData;
import de.njsm.stocks.client.business.entities.StoredFoodAmount;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class SearchRepositoryImpl implements SearchRepository {

    private final SearchDao searchDao;

    private final Clock clock;

    @Inject
    SearchRepositoryImpl(SearchDao searchDao, Clock clock) {
        this.searchDao = searchDao;
        this.clock = clock;
    }

    public Cursor search(String query) {
        return searchDao.search(query, buildContiguousQueryString(query), buildSubsequenceQueryString(query));
    }

    private String buildContiguousQueryString(String query) {
        return '%' + query + '%';
    }

    private String buildSubsequenceQueryString(String query) {
        StringBuilder queryBuilder = new StringBuilder(query.length() * 2 + 1);
        queryBuilder.append('%');
        for (char c : query.toCharArray()) {
            queryBuilder.append(c);
            queryBuilder.append('%');
        }
        return queryBuilder.toString();
    }

    @Override
    public void storeRecentSearch(String query) {
        RecentSearchDbEntity searchSuggestion = RecentSearchDbEntity.create(query, clock.get());
        searchDao.store(searchSuggestion);
    }

    @Override
    public void storeFoundFood(Id<Food> food) {
        SearchedFoodDbEntity searchedFood = SearchedFoodDbEntity.create(food.id(), clock.get());
        searchDao.store(searchedFood);
    }

    @Override
    public Observable<List<SearchedFoodForListingBaseData>> getFoodBy(String query) {
        return searchDao.getFoodBy(buildContiguousQueryString(query));
    }

    @Override
    public Observable<List<StoredFoodAmount>> getFoodAmountsIn(String query) {
        return searchDao.getFoodAmountsIn(buildContiguousQueryString(query));
    }

    @Override
    public Observable<List<StoredFoodAmount>> getFoodDefaultUnitOfFoodWithoutItems(String query) {
        return searchDao.getFoodAmountsOfAbsentFood(buildContiguousQueryString(query));
    }

    @Override
    public void deleteSearchHistory() {
        searchDao.deleteRecentSearches();
        searchDao.deleteFoudFood();
    }
}

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

package de.njsm.stocks.client.presenter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import de.njsm.stocks.client.business.EntityDeleter;
import de.njsm.stocks.client.business.RecipeListInteractor;
import de.njsm.stocks.client.business.Synchroniser;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.Recipe;
import de.njsm.stocks.client.business.entities.RecipeForListing;
import de.njsm.stocks.client.business.entities.RecipesForListing;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class RecipeListViewModel extends ViewModel {

    private final RecipeListInteractor interactor;

    private final Synchroniser synchroniser;

    private final ObservableDataCache<RecipesForListing> data;

    private final EntityDeleter<Recipe> deleter;

    public RecipeListViewModel(RecipeListInteractor interactor, Synchroniser synchroniser, ObservableDataCache<RecipesForListing> data, EntityDeleter<Recipe> deleter) {
        this.interactor = interactor;
        this.synchroniser = synchroniser;
        this.data = data;
        this.deleter = deleter;
    }

    public LiveData<RecipesForListing> get() {
        return data.getLiveData(interactor::get);
    }

    public void delete(int listItemIndex, boolean sortedByName) {
        Function<RecipesForListing, List<RecipeForListing>> listSelector;
        if (sortedByName)
            listSelector = RecipesForListing::byName;
        else
            listSelector = RecipesForListing::byCookability;
        data.performOnNestedList(listItemIndex, listSelector, deleter::delete);
    }

    public void synchronise() {
        synchroniser.synchronise();
    }

    public void resolveId(int listItemIndex, boolean sortedByName, Consumer<Id<Recipe>> callback) {
        Function<RecipesForListing, List<RecipeForListing>> listSelector;
        if (sortedByName)
            listSelector = RecipesForListing::byName;
        else
            listSelector = RecipesForListing::byCookability;
        data.performOnNestedList(listItemIndex, listSelector, callback::accept);
    }

    @Override
    protected void onCleared() {
        data.clear();
    }
}

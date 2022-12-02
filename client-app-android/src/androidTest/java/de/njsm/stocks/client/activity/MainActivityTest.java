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

package de.njsm.stocks.client.activity;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import de.njsm.stocks.client.TestApplication;
import de.njsm.stocks.client.business.SearchInteractor;
import de.njsm.stocks.client.business.entities.Food;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.navigation.NavigationArgConsumerImpl;
import de.njsm.stocks.client.navigation.NavigationGraphDirections;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

public class MainActivityTest {

    private ActivityScenario<MainActivity> scenario;

    private SearchInteractor interactor;

    private NavigationArgConsumerImpl navigationArgConsumer;

    @Before
    public void setUp() {
        ((TestApplication) ApplicationProvider.getApplicationContext()).getDaggerRoot().inject(this);
        Intents.init();
    }

    @After
    public void tearDown() {
        Intents.release();
    }

    @Test
    public void searchingForFoodNavigatesToSearchResults() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_SEARCH);
        String query = "search term";
        intent.putExtra(SearchManager.QUERY, query);

        scenario = ActivityScenario.launch(intent);

        verify(interactor).storeRecentSearch(query);
        verify(navigationArgConsumer).navigate(NavigationGraphDirections.actionGlobalNavFragmentSearchResults(query));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void selectingFoodNavigatesToFoodDetails() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        int foodId = 42;
        intent.setData(Uri.parse("content://de.njsm.stocks.client.database.contentprovider.SearchSuggestionsProvider/food/" + foodId));

        scenario = ActivityScenario.launch(intent);

        ArgumentCaptor<Id<Food>> captor = ArgumentCaptor.forClass(Id.class);
        verify(interactor).storeFoundFood(captor.capture());
        assertEquals(foodId, captor.getValue().id());
        verify(navigationArgConsumer).navigate(NavigationGraphDirections.actionGlobalNavFragmentFood(foodId));
    }

    @Inject
    void setInteractor(SearchInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    void setNavigationArgConsumer(NavigationArgConsumerImpl navigationArgConsumer) {
        this.navigationArgConsumer = navigationArgConsumer;
    }
}

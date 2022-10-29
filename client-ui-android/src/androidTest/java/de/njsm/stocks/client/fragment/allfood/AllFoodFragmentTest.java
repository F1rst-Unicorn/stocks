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

package de.njsm.stocks.client.fragment.allfood;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.business.FakeAllPresentFoodListInteractor;
import de.njsm.stocks.client.business.entities.FoodForListing;
import de.njsm.stocks.client.navigation.AllFoodNavigator;
import de.njsm.stocks.client.navigation.FoodNavigator;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.ui.R;
import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import java.util.List;

import static org.mockito.Mockito.reset;

public class AllFoodFragmentTest extends BaseFoodFragmentTest {

    private FragmentScenario<AllFoodFragment> scenario;

    private AllFoodNavigator navigator;

    private FakeAllPresentFoodListInteractor allPresentFoodListInteractor;

    @Before
    public void setUp() {
        ((Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext()).getDaggerRoot().inject(this);
        scenario = FragmentScenario.launchInContainer(AllFoodFragment.class, new Bundle(), R.style.StocksTheme);
        dateRenderStrategy = new DateRenderStrategy(localiser);
    }

    @After
    public void tearDown() {
        reset(navigator);
    }

    @Override
    protected FoodNavigator navigator() {
        return navigator;
    }

    @Override
    protected void setData(List<FoodForListing> data) {
        allPresentFoodListInteractor.setData(data);
    }

    @Override
    protected int getEmptyText() {
        return R.string.hint_no_food_items;
    }

    @Inject
    void setNavigator(AllFoodNavigator navigator) {
        this.navigator = navigator;
    }

    @Inject
    void setAllPresentFoodListInteractor(FakeAllPresentFoodListInteractor allPresentFoodListInteractor) {
        this.allPresentFoodListInteractor = allPresentFoodListInteractor;
    }
}
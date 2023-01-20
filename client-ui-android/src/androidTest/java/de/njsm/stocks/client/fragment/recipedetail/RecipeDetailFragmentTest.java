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

package de.njsm.stocks.client.fragment.recipedetail;

import android.os.Bundle;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.platform.app.InstrumentationRegistry;
import de.njsm.stocks.client.Application;
import de.njsm.stocks.client.Matchers;
import de.njsm.stocks.client.business.RecipeDetailInteractor;
import de.njsm.stocks.client.business.entities.RecipeForDetails;
import de.njsm.stocks.client.business.entities.RecipeIngredientForDetails;
import de.njsm.stocks.client.business.entities.RecipeProductForDetails;
import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.navigation.RecipeDetailNavigator;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static de.njsm.stocks.client.fragment.Util.menuItem;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class RecipeDetailFragmentTest {

    private Application application;

    private FragmentScenario<RecipeDetailFragment> scenario;

    private RecipeDetailInteractor interactor;

    private RecipeDetailNavigator navigator;

    private UnitAmountRenderStrategy unitAmountRenderStrategy;

    @Before
    public void setup() {
        application = (Application) InstrumentationRegistry.getInstrumentation().getTargetContext().getApplicationContext();
        application.getDaggerRoot().inject(this);
        reset(interactor);
        reset(navigator);
        when(interactor.get(any())).thenReturn(Observable.just(getInput()));
        when(navigator.getRecipe(any())).thenReturn(getInput());
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
        scenario = FragmentScenario.launchInContainer(RecipeDetailFragment.class, new Bundle(), R.style.StocksTheme);
    }

    @Test
    public void uiIsShown() {
        onView(withId(R.id.fragment_recipe_details_instructions))
                .check(matches(withText(getInput().instructions())));
        onView(withId(R.id.fragment_recipe_details_duration))
                .check(matches(withText(String.valueOf(getInput().duration().toMinutes()))));

        RecipeIngredientForDetails ingredient = getInput().ingredients().get(0);
        onView(withId(R.id.fragment_recipe_details_ingredient_list))
                .check(matches(withText(
                        String.format(application.getString(R.string.dialog_recipe_item),
                                unitAmountRenderStrategy.render(ingredient.neededAmount()),
                                ingredient.foodName(),
                                unitAmountRenderStrategy.render(ingredient.storedAmounts())
                ))));

        RecipeProductForDetails product = getInput().products().get(0);
        onView(withId(R.id.fragment_recipe_details_product_list))
                .check(matches(withText(
                        String.format(application.getString(R.string.dialog_recipe_item),
                                unitAmountRenderStrategy.render(product.neededAmount()),
                                product.foodName(),
                                unitAmountRenderStrategy.render(product.storedAmounts())
                ))));
    }

    @Test
    public void editingNavigates() {
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_recipe_details_edit)));

        verify(navigator).edit(Matchers.equalBy(getInput()));
    }

    @Test
    public void preparingNavigates() {
        scenario.onFragment(v -> v.onMenuItemSelected(menuItem(v.requireContext(), R.id.menu_recipe_details_prepare)));

        verify(navigator).prepare(Matchers.equalBy(getInput()));
    }

    private RecipeForDetails getInput() {
        return RecipeForDetails.create(
                1,
                "Pizza",
                Duration.ofMinutes(45),
                "Just do it",
                List.of(
                        RecipeIngredientForDetails.create(
                                2,
                                "Water",
                                UnitAmount.of(BigDecimal.ONE, "gramm"),
                                List.of(UnitAmount.of(BigDecimal.ONE, "gramm"))
                        )
                ),
                List.of(
                        RecipeProductForDetails.create(
                                2,
                                "Pizza",
                                UnitAmount.of(BigDecimal.ONE, "gramm"),
                                List.of(UnitAmount.of(BigDecimal.ONE, "gramm"))
                        )
                )
        );
    }

    @Inject
    void setInteractor(RecipeDetailInteractor interactor) {
        this.interactor = interactor;
    }

    @Inject
    void setNavigator(RecipeDetailNavigator navigator) {
        this.navigator = navigator;
    }
}

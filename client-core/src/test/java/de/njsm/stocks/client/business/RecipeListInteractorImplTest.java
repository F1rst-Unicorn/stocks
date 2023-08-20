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

import de.njsm.stocks.client.business.entities.RecipeForListing;
import de.njsm.stocks.client.business.entities.RecipeForListingBaseData;
import de.njsm.stocks.client.business.entities.RecipeIngredientAmount;
import de.njsm.stocks.client.business.entities.RecipesForListing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeListInteractorImplTest {

    private RecipeListInteractor uut;

    @Mock
    RecipeListRepository repository;

    @BeforeEach
    void setUp() {
        uut = new RecipeListInteractorImpl(repository);
    }

    @Test
    void recipeWithoutIngredientsIsCookable() {
        test(RecipeForListingBaseData.create(1, "Pizza"), 7, 7);
    }

    @Test
    void singleMissingIngredientMakesUncookable() {
        test(RecipeForListingBaseData.create(1, "Pizza"), 0, 0, missingAmount());
    }

    @Test
    void singleNecessaryIngredientMakesUncookable() {
        test(RecipeForListingBaseData.create(1, "Pizza"), 7, 0, necessaryAmount());
    }

    @Test
    void singleSufficientIngredientMakesUncookable() {
        test(RecipeForListingBaseData.create(1, "Pizza"), 7, 7, sufficientAmount());
    }

    @Test
    void oneNecessaryAmountIsAveraged() {
        test(RecipeForListingBaseData.create(1, "Pizza"), 3, 0, necessaryAmount(), missingAmount());
    }

    @Test
    void oneSufficientAmountIsAveraged() {
        test(RecipeForListingBaseData.create(1, "Pizza"), 7, 3, sufficientAmount(), necessaryAmount());
    }

    @Test
    void oneSufficientOneMissingAmountIsAveraged() {
        test(RecipeForListingBaseData.create(1, "Pizza"), 3, 3, sufficientAmount(), missingAmount());
    }

    @Test
    void noRecipesGivesEmptyList() {
        when(repository.get()).thenReturn(Observable.just(emptyList()));
        when(repository.getIngredients()).thenReturn(Observable.just(emptyList()));

        var actual = uut.get();

        actual.test()
                .awaitCount(1)
                .assertNoErrors()
                .assertValue(RecipesForListing.create(emptyList(), emptyList()));
    }

    public RecipeIngredientAmount sufficientAmount() {
        return RecipeIngredientAmount.create(1, RecipeIngredientAmount.Amount.create(1, BigDecimal.ONE, 1),
                List.of(RecipeIngredientAmount.Amount.create(1, BigDecimal.ONE, 1)));
    }

    public RecipeIngredientAmount necessaryAmount() {
        return RecipeIngredientAmount.create(1, RecipeIngredientAmount.Amount.create(1, BigDecimal.ONE, 1),
                List.of(RecipeIngredientAmount.Amount.create(2, BigDecimal.ONE, 1)));
    }

    public RecipeIngredientAmount missingAmount() {
        return RecipeIngredientAmount.create(1, RecipeIngredientAmount.Amount.create(1, BigDecimal.ONE, 1), emptyList());
    }

    private void test(RecipeForListingBaseData recipe,
                      int expectedNecessaryIndex,
                      int expectedSufficientIndex,
                      RecipeIngredientAmount... amounts) {
        when(repository.get()).thenReturn(Observable.just(List.of(recipe)));
        when(repository.getIngredients()).thenReturn(Observable.just(Arrays.asList(amounts)));

        var actual = uut.get();

        actual.test()
                .awaitCount(1)
                .assertNoErrors()
                .assertValue(RecipesForListing.create(
                        List.of(RecipeForListing.create(1, "Pizza", expectedNecessaryIndex, expectedSufficientIndex)),
                        List.of(RecipeForListing.create(1, "Pizza", expectedNecessaryIndex, expectedSufficientIndex))

        ));
    }
}
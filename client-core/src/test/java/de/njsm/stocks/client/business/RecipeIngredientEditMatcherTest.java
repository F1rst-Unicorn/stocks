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
import org.junit.jupiter.api.Test;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static java.util.List.of;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

class RecipeIngredientEditMatcherTest {

    private final VersionedId<RecipeIngredient> dummyVersion = VersionedId.create(1, -1);

    private RecipeIngredientEditMatcher uut;

    @Test
    void singleIngredientIsAdded() {
        RecipeIngredientEditFormData formItem = RecipeIngredientEditFormData.create(
                1, 2, 3, create(4), 5, create(6)
        );

        uut = new RecipeIngredientEditMatcher(of(), of(formItem),
                this::dummyVersion);

        assertThat(uut.getToAdd(), is(of(RecipeIngredientToAdd.create(formItem.amount(), formItem.ingredient(), formItem.unit()))));
        assertThat(uut.getToEdit(), is(empty()));
        assertThat(uut.getToDelete(), is(empty()));
    }

    @Test
    void singleIngredientIsDeleted() {
        RecipeIngredientEditData presentItem = RecipeIngredientEditData.create(
                1, 2, create(3), create(4)
        );

        uut = new RecipeIngredientEditMatcher(of(presentItem), of(),
                this::dummyVersion);

        assertThat(uut.getToDelete(), is(of(RecipeIngredientDeleteNetworkData.create(presentItem.id(), dummyVersion.version()))));
        assertThat(uut.getToEdit(), is(empty()));
        assertThat(uut.getToAdd(), is(empty()));
    }

    @Test
    void singleMatchingIsMatched() {
        RecipeIngredientEditData presentItem = RecipeIngredientEditData.create(
                1, 2, create(3), create(4)
        );
        RecipeIngredientEditFormData formItem = RecipeIngredientEditFormData.create(
                5, 6, -2, create(7), -2, create(8)
        );

        uut = new RecipeIngredientEditMatcher(of(presentItem), of(formItem),
                this::dummyVersion);

        assertThat(uut.getToDelete(), is(empty()));
        assertThat(uut.getToEdit(), is(of(RecipeIngredientEditNetworkData.create(
                presentItem.id(), dummyVersion.version(), formItem.amount(), formItem.unit(), formItem.ingredient()
        ))));
        assertThat(uut.getToAdd(), is(empty()));
    }

    @Test
    void higherMatchingFormItemIsPreferred() {
        RecipeIngredientEditData presentItem = RecipeIngredientEditData.create(
                1, 2, create(3), create(4)
        );
        RecipeIngredientEditFormData formItem = RecipeIngredientEditFormData.create(
                5, 6, -2, create(7), -2, presentItem.ingredient()
        );
        RecipeIngredientEditFormData lowerRatedFormItem = RecipeIngredientEditFormData.create(
                5, 6, -2, create(7), -2, create(8)
        );

        uut = new RecipeIngredientEditMatcher(of(presentItem), of(lowerRatedFormItem, formItem),
                this::dummyVersion);

        assertThat(uut.getToDelete(), is(empty()));
        assertThat(uut.getToEdit(), is(of(RecipeIngredientEditNetworkData.create(
                presentItem.id(), dummyVersion.version(), formItem.amount(), formItem.unit(), formItem.ingredient()
        ))));
        assertThat(uut.getToAdd(), is(of(RecipeIngredientToAdd.create(lowerRatedFormItem.amount(), lowerRatedFormItem.ingredient(), lowerRatedFormItem.unit()))));
    }

    @Test
    void higherMatchingPresentItemIsPreferred() {
        RecipeIngredientEditData presentItem = RecipeIngredientEditData.create(
                1, 2, create(3), create(4)
        );
        RecipeIngredientEditData lowerRatedPresentItem = RecipeIngredientEditData.create(
                1, 2, create(3), create(4)
        );
        RecipeIngredientEditFormData formItem = RecipeIngredientEditFormData.create(
                5, 6, -2, create(7), -2, presentItem.ingredient()
        );

        uut = new RecipeIngredientEditMatcher(of(lowerRatedPresentItem, presentItem), of(formItem),
                this::dummyVersion);

        assertThat(uut.getToDelete(), is(of(RecipeIngredientDeleteNetworkData.create(lowerRatedPresentItem.id(), dummyVersion.version()))));
        assertThat(uut.getToEdit(), is(of(RecipeIngredientEditNetworkData.create(
                presentItem.id(), dummyVersion.version(), formItem.amount(), formItem.unit(), formItem.ingredient()
        ))));
        assertThat(uut.getToAdd(), is(empty()));
    }

    private Versionable<RecipeIngredient> dummyVersion(Id<RecipeIngredient> v) {
        return dummyVersion;
    }
}
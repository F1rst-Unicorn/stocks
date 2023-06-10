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

package de.njsm.stocks.client.database.error;

import de.njsm.stocks.client.business.StatusCodeException;
import de.njsm.stocks.client.business.entities.*;
import org.junit.Test;

import java.time.Duration;
import java.util.List;

import static de.njsm.stocks.client.business.entities.IdImpl.create;
import static org.junit.Assert.assertTrue;

public class RecipeEditErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    @Override
    ErrorDetails recordError(StatusCodeException e) {
        RecipeEditForm input = RecipeEditForm.create(
                RecipeEditBaseData.create(1, "Pizza", "just bake", Duration.ofMinutes(2)),
                List.of(RecipeIngredientEditFormData.create(
                        3, 4, -1, create(5), -1, create(6)
                )),
                List.of(RecipeProductEditFormData.create(
                        7, 8, -1, create(9), -1, create(10)
                )));
        errorRecorder.recordRecipeEditError(e, input);
        return input;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getRecipeEdits();
    }

    @Test
    @Override
    public void deletingErrorWorks() {
        super.deletingErrorWorks();
        assertTrue(stocksDatabase.errorDao().getRecipeIngredientEdits().isEmpty());
        assertTrue(stocksDatabase.errorDao().getRecipeProductEdits().isEmpty());
    }
}

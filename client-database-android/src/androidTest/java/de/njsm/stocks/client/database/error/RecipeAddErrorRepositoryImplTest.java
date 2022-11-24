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

import static org.junit.Assert.assertTrue;

public class RecipeAddErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    @Override
    ErrorDetails recordError(StatusCodeException e) {
        RecipeAddForm form = RecipeAddForm.create("Pizza", "just bake", Duration.ofMinutes(5),
                List.of(RecipeIngredientToAdd.create(1, IdImpl.create(2), IdImpl.create(3))),
                List.of(RecipeProductToAdd.create(4, IdImpl.create(5), IdImpl.create(6))));
        errorRecorder.recordRecipeAddError(e, form);
        return form;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getRecipeAdds();
    }

    @Test
    @Override
    public void deletingErrorWorks() {
        super.deletingErrorWorks();
        assertTrue(stocksDatabase.errorDao().getRecipeIngredientAdds().isEmpty());
        assertTrue(stocksDatabase.errorDao().getRecipeProductAdds().isEmpty());
    }
}

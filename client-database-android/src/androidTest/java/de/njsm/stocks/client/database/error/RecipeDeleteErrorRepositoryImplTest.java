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
import de.njsm.stocks.client.database.RecipeDbEntity;
import de.njsm.stocks.client.database.UpdateDbEntity;
import org.junit.Test;

import java.time.Instant;
import java.util.List;

import static de.njsm.stocks.client.database.BitemporalOperations.currentDelete;
import static de.njsm.stocks.client.database.BitemporalOperations.sequencedDeleteOfEntireTime;
import static de.njsm.stocks.client.database.util.Util.test;
import static de.njsm.stocks.client.database.util.Util.testList;
import static java.util.Collections.singletonList;

public class RecipeDeleteErrorRepositoryImplTest extends AbstractErrorRepositoryImplTest {

    ErrorDetails recordError(StatusCodeException e) {
        RecipeDbEntity recipe = standardEntities.recipeDbEntity();
        stocksDatabase.synchronisationDao().writeRecipes(singletonList(recipe));
        Id<Recipe> data = IdImpl.create(recipe.id());
        RecipeDeleteErrorDetails errorDetails = RecipeDeleteErrorDetails.create(recipe.id(), recipe.name());
        errorRecorder.recordRecipeDeleteError(e, data);
        return errorDetails;
    }

    @Override
    List<?> getErrorDetails() {
        return stocksDatabase.errorDao().getRecipeDeletes();
    }

    @Test
    public void gettingErrorOfDeletedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        RecipeDbEntity recipe = standardEntities.recipeDbEntity();
        Id<Recipe> data = IdImpl.create(recipe.id());
        stocksDatabase.synchronisationDao().writeRecipes(singletonList(recipe));
        stocksDatabase.synchronisationDao().writeRecipes(currentDelete(recipe, editTime));
        errorRecorder.recordRecipeDeleteError(exception, data);
        ErrorDetails errorDetails = RecipeDeleteErrorDetails.create(recipe.id(), recipe.name());

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(errorDetails));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }

    @Test
    public void gettingErrorOfInvalidatedEntityWorks() {
        Instant editTime = Instant.EPOCH.plusSeconds(5);
        StatusCode statusCode = StatusCode.DATABASE_UNREACHABLE;
        StatusCodeException exception = new StatusCodeException(statusCode);
        RecipeDbEntity recipe = standardEntities.recipeDbEntity();
        Id<Recipe> data = IdImpl.create(recipe.id());
        stocksDatabase.synchronisationDao().writeRecipes(singletonList(recipe));
        stocksDatabase.synchronisationDao().writeRecipes(sequencedDeleteOfEntireTime(recipe, editTime));
        stocksDatabase.synchronisationDao().insert(singletonList(UpdateDbEntity.create(EntityType.FOOD, Instant.EPOCH)));
        errorRecorder.recordRecipeDeleteError(exception, data);
        ErrorDetails errorDetails = RecipeDeleteErrorDetails.create(recipe.id(), recipe.name());

        test(uut.getNumberOfErrors()).assertValue(1);
        testList(uut.getErrors()).assertValue(v -> v.get(0).statusCode() == statusCode);
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorDetails().equals(errorDetails));
        testList(uut.getErrors()).assertValue(v -> v.get(0).errorMessage().equals(exception.getMessage()));
    }
}

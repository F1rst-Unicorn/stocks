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

import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class RecipeInsertionTest extends InsertionTest<RecipeDbEntity, RecipeDbEntity.Builder> {

    @Override
    RecipeDbEntity.Builder getFreshDto() {
        return standardEntities.recipeDbEntityBuilder();
    }

    @Override
    List<RecipeDbEntity> getAll() {
        return stocksDatabase.recipeDao().getAll();
    }

    @Override
    void insert(List<RecipeDbEntity> data, SynchronisationDao synchronisationDao) {
        synchronisationDao.writeRecipes(data);
    }

    @Override
    void synchronise(List<RecipeDbEntity> data, SynchronisationDao synchronisationDao) {
        synchronisationDao.synchroniseRecipes(data);
    }
}

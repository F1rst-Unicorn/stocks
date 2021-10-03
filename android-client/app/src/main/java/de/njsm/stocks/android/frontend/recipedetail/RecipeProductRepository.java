/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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
 */

package de.njsm.stocks.android.frontend.recipedetail;

import androidx.lifecycle.LiveData;
import de.njsm.stocks.android.db.dao.RecipeProductDao;
import de.njsm.stocks.android.db.entities.RecipeProduct;
import de.njsm.stocks.android.db.views.ScaledFood;

import javax.inject.Inject;
import java.util.List;

public class RecipeProductRepository {

    private final RecipeProductDao recipeProductDao;

    @Inject
    public RecipeProductRepository(RecipeProductDao recipeProductDao) {
        this.recipeProductDao = recipeProductDao;
    }


    public LiveData<List<ScaledFood>> getProductViewsOf(int recipeId) {
        return recipeProductDao.getProductViewsOf(recipeId);
    }

    public LiveData<List<RecipeProduct>> getProductsOf(int recipeId) {
        return recipeProductDao.getLiveProductsOf(recipeId);
    }
}

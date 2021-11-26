/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.db;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import de.njsm.stocks.android.db.dao.Inserter;
import de.njsm.stocks.android.db.dao.RecipeProductDao;
import de.njsm.stocks.android.db.entities.RecipeProduct;
import de.njsm.stocks.android.util.Config;
import org.junit.Before;
import org.junit.runner.RunWith;
import java.time.Instant;

@RunWith(AndroidJUnit4.class)
public class RecipeProductDaoTest extends InsertionTest<RecipeProduct> {

    private RecipeProductDao recipeProductDao;

    @Before
    public void setup() {
        recipeProductDao = stocksDatabase.recipeProductDao();
    }

    @Override
    Inserter<RecipeProduct> getDao() {
        return recipeProductDao;
    }

    @Override
    RecipeProduct getDto() {
        return new RecipeProduct(1, Instant.EPOCH, Config.API_INFINITY, Instant.EPOCH, Config.API_INFINITY, 0, 1, 2, 3, 4, 5);
    }

    @Override
    void alterDto(RecipeProduct data) {
        data.unit = 7;
    }

}

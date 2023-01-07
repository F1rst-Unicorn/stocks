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

import de.njsm.stocks.client.business.FoodItemEditRepository;
import de.njsm.stocks.client.business.entities.FoodForSelection;
import de.njsm.stocks.client.business.entities.FoodItemEditBaseData;
import de.njsm.stocks.client.business.entities.FoodItemForEditing;
import io.reactivex.rxjava3.core.Observable;
import org.junit.Before;
import org.junit.Test;

import static de.njsm.stocks.client.database.util.Util.test;
import static java.util.List.of;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class FoodItemEditRepositoryImplTest extends DbTestCase {

    private FoodItemEditRepository uut;

    @Before
    public void setUp() {
        uut = new FoodItemEditRepositoryImpl(null, null, stocksDatabase.foodItemDao());
    }

    @Test
    public void gettingDataForFormWorks() {
        FoodDbEntity food = standardEntities.foodDbEntity();
        FoodItemDbEntity data = standardEntities.foodItemDbEntityBuilder()
                .ofType(food.id())
                .build();
        stocksDatabase.synchronisationDao().writeFoodItems(of(data));
        stocksDatabase.synchronisationDao().writeFood(of(food));

        Observable<FoodItemEditBaseData> actual = uut.getFoodItem(data::id);

        test(actual).assertValue(FoodItemEditBaseData.create(
                data.id(),
                FoodForSelection.create(food.id(), food.name()),
                data.eatBy(),
                data.storedIn(),
                data.unit()
        ));
    }

    @Test
    public void gettingDataForEditingWorks() {
        var data = standardEntities.foodItemDbEntity();
        stocksDatabase.synchronisationDao().writeFoodItems(of(data));

        var actual = uut.getFoodItemForSending(data::id);

        assertThat(actual, is(equalTo(FoodItemForEditing.create(
                data.id(),
                data.version(),
                data.eatBy(),
                data.storedIn(),
                data.unit()
        ))));
    }
}
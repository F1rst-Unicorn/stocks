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

package de.njsm.stocks.android.business.data.eventlog.differ;

import de.njsm.stocks.R;
import de.njsm.stocks.android.business.data.eventlog.PartialDiffGenerator;
import de.njsm.stocks.android.business.data.eventlog.SentenceObject;
import de.njsm.stocks.android.db.views.FoodItemViewWithFood;

import java.util.function.IntFunction;

public class FoodItemLocationChangedGenerator extends PartialDiffGenerator<FoodItemViewWithFood> {

    public FoodItemLocationChangedGenerator(IntFunction<String> stringResourceResolver, FoodItemViewWithFood oldEntity, FoodItemViewWithFood newEntity, SentenceObject object) {
        super(stringResourceResolver, oldEntity, newEntity, object);
    }

    @Override
    protected boolean entitiesDiffer() {
        return !getOldEntity().getLocation().equals(getNewEntity().getLocation());
    }

    @Override
    protected int getStringId() {
        return R.string.event_food_item_changed_location;
    }

    @Override
    protected Object[] getFormatArguments() {
        return new Object[] {
                getObject().get(),
                getOldEntity().getLocation().getName(),
                getNewEntity().getLocation().getName()
        };
    }
}
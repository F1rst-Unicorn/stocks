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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.FoodItem;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.impl.FoodItemForDeletion;
import de.njsm.stocks.common.api.impl.FoodItemForEditing;
import de.njsm.stocks.common.api.impl.FoodItemForInsertion;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodItemRecord;

public class FoodItemManager extends BusinessObject<FoodItemRecord, FoodItem> implements
        BusinessGettable<FoodItemRecord, FoodItem>,
        BusinessDeletable<FoodItemForDeletion, FoodItem> {

    private final FoodItemHandler dbHandler;

    private final FoodHandler foodHandler;

    public FoodItemManager(FoodItemHandler dbHandler, FoodHandler foodHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        this.foodHandler = foodHandler;
    }

    public StatusCode add(FoodItemForInsertion item) {
        return runOperation(() -> dbHandler.add(item)
                .bind(() -> foodHandler.setToBuyStatus(item.getOfTypeFood(), false)));
    }

    public StatusCode edit(FoodItemForEditing item) {
        return runOperation(() -> dbHandler.edit(item));
    }

    public StatusCode delete(FoodItemForDeletion item) {
        return runOperation(() -> dbHandler.delete(item));
    }

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
        foodHandler.setPrincipals(principals);
    }
}

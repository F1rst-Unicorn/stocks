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

import de.njsm.stocks.common.api.*;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.FoodRecord;

import java.time.Period;

public class FoodManager extends BusinessObject<FoodRecord, Food> implements
        BusinessGettable<FoodRecord, Food>,
        BusinessAddable<FoodRecord, Food>,
        BusinessDeletable<FoodForDeletion, Food>{

    private final FoodHandler dbHandler;

    private final EanNumberHandler eanNumberHandler;

    private final FoodItemHandler foodItemHandler;

    public FoodManager(FoodHandler dbHandler, FoodItemHandler foodItemHandler, EanNumberHandler eanNumberHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        this.foodItemHandler = foodItemHandler;
        this.eanNumberHandler = eanNumberHandler;
    }

    public StatusCode rename(FoodForEditing item) {
        return runOperation(() -> dbHandler.edit(FoodForFullEditing.builder()
                .id(item.id())
                .version(item.version())
                .name(item.name())
                .location(item.location().orElse(null))
                .storeUnit(item.storeUnit().orElse(null))
                .toBuy(null)
                .expirationOffset(item.expirationOffset().map(Period::getDays).orElse(null))
                .description(item.description().orElse(null))
                .build()));
    }

    public StatusCode edit(FoodForFullEditing food) {
        return runOperation(() -> dbHandler.edit(food));
    }

    public StatusCode setToBuyStatus(FoodForSetToBuy food) {
        return runOperation(() -> dbHandler.setToBuyStatus(food));
    }

    public StatusCode delete(FoodForDeletion item) {
        return runOperation(() -> eanNumberHandler.deleteOwnedByFood(item)
                .bind(() -> foodItemHandler.deleteItemsOfType(item))
                .bind(() -> dbHandler.delete(item)));
    }

    @Override
    public void setPrincipals(Principals principals) {
        super.setPrincipals(principals);
        foodItemHandler.setPrincipals(principals);
        eanNumberHandler.setPrincipals(principals);
    }

    public StatusCode setDescription(FoodForSetDescription item) {
        return runOperation(() -> dbHandler.setDescription(item));
    }
}

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

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.data.*;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import fj.data.Validation;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

public class FoodManager extends BusinessObject {

    private final FoodHandler dbHandler;

    private final EanNumberHandler eanNumberHandler;

    private final FoodItemHandler foodItemHandler;

    public FoodManager(FoodHandler dbHandler, FoodItemHandler foodItemHandler, EanNumberHandler eanNumberHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        this.foodItemHandler = foodItemHandler;
        this.eanNumberHandler = eanNumberHandler;
    }

    public Validation<StatusCode, Integer> add(FoodForInsertion item) {
        return runFunction(() -> dbHandler.add(item));
    }

    public Validation<StatusCode, Stream<Food>> get(AsyncResponse r, boolean bitemporal, Instant startingFrom) {
        return runAsynchronously(r, () -> {
            dbHandler.setReadOnly();
            return dbHandler.get(bitemporal, startingFrom);
        });
    }

    public StatusCode rename(FoodForEditing item) {
        return runOperation(() -> dbHandler.edit(item));
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

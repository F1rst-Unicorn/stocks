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

import de.njsm.stocks.server.v2.business.data.Food;
import de.njsm.stocks.server.v2.business.data.FoodItem;
import de.njsm.stocks.server.v2.db.FoodHandler;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import fj.data.Validation;

import javax.ws.rs.container.AsyncResponse;
import java.time.Instant;
import java.util.stream.Stream;

public class FoodItemManager extends BusinessObject {

    private final FoodItemHandler dbHandler;

    private final FoodHandler foodHandler;

    public FoodItemManager(FoodItemHandler dbHandler, FoodHandler foodHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
        this.foodHandler = foodHandler;
    }

    public Validation<StatusCode, Integer> add(FoodItem item) {
        return runFunction(() -> {
            Validation<StatusCode, Integer> result = dbHandler.add(item);
            return result.bind(v -> foodHandler.setToBuyStatus(new Food(item.ofType, -1), false)
                    .toValidation().map((__) -> v));
        });
    }

    public Validation<StatusCode, Stream<FoodItem>> get(AsyncResponse r, boolean bitemporal, Instant startingFrom) {
        return runAsynchronously(r, () -> {
            dbHandler.setReadOnly();
            return dbHandler.get(bitemporal, startingFrom);
        });
    }

    public StatusCode edit(FoodItem item) {
        return runOperation(() -> dbHandler.edit(item));
    }

    public StatusCode delete(FoodItem item) {
        return runOperation(() -> dbHandler.delete(item));
    }
}

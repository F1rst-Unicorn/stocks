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

package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.common.api.RecipeIngredient;
import de.njsm.stocks.server.v2.business.RecipeIngredientManager;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeIngredientRecord;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

@RequestMapping("v2/recipe-ingredient")
@RestController
@RequestScope
public class RecipeIngredientEndpoint extends Endpoint implements Get<RecipeIngredientRecord, RecipeIngredient> {

    private final RecipeIngredientManager manager;

    public RecipeIngredientEndpoint(RecipeIngredientManager manager) {
        this.manager = manager;
    }

    @Override
    public RecipeIngredientManager getManager() {
        return manager;
    }
}

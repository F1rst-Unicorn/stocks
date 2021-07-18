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

import de.njsm.stocks.server.v2.business.data.Recipe;
import de.njsm.stocks.server.v2.business.data.RecipeForDeletion;
import de.njsm.stocks.server.v2.db.RecipeHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeRecord;

public class RecipeManager extends BusinessObject<RecipeRecord, Recipe>
        implements BusinessGettable<RecipeRecord, Recipe>,
                   BusinessDeletable<RecipeForDeletion, Recipe> {

    private final RecipeHandler dbHandler;

    public RecipeManager(RecipeHandler dbHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
    }

    public StatusCode delete(RecipeForDeletion recipe) {
        return runOperation(() -> dbHandler.delete(recipe));
    }
}

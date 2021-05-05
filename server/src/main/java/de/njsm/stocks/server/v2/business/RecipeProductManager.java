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

import de.njsm.stocks.server.v2.business.data.RecipeProduct;
import de.njsm.stocks.server.v2.business.data.RecipeProductForDeletion;
import de.njsm.stocks.server.v2.db.RecipeProductHandler;
import de.njsm.stocks.server.v2.db.jooq.tables.records.RecipeProductRecord;

public class RecipeProductManager extends BusinessObject<RecipeProductRecord, RecipeProduct>
        implements BusinessGettable<RecipeProductRecord, RecipeProduct>,
                   BusinessDeletable<RecipeProductForDeletion, RecipeProduct> {

    private final RecipeProductHandler dbHandler;

    public RecipeProductManager(RecipeProductHandler dbHandler) {
        super(dbHandler);
        this.dbHandler = dbHandler;
    }

    public StatusCode delete(RecipeProductForDeletion Recipe) {
        return runOperation(() -> dbHandler.delete(Recipe));
    }
}

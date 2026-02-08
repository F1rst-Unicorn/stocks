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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.Principals;
import org.jooq.TableRecord;
import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import org.springframework.security.core.context.SecurityContextHolder;

public class BusinessObject<U extends TableRecord<U>, T extends Entity<T>> implements BusinessOperations {

    private final CrudDatabaseHandler<U, T> dbHandler;

    public BusinessObject(CrudDatabaseHandler<U, T> dbHandler) {
        this.dbHandler = dbHandler;
    }

    @Override
    public CrudDatabaseHandler<U, T> getDbHandler() {
        return dbHandler;
    }

    protected Principals getPrincipals() {
        return (Principals) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

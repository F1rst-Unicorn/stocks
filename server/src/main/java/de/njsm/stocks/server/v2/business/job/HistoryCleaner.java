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

package de.njsm.stocks.server.v2.business.job;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import de.njsm.stocks.server.v2.db.PrincipalsHandler;
import org.springframework.security.authentication.AuthenticationManager;

import java.time.Period;
import java.util.List;

public class HistoryCleaner extends QuartzJob {

    private final Period maxHistory;

    private final List<CrudDatabaseHandler<?, ?>> tableHandlers;

    public HistoryCleaner(
            Period maxHistory,
            PrincipalsHandler principalsHandler,
            List<CrudDatabaseHandler<?, ?>> tableHandlers,
            AuthenticationManager authenticationManager
    ) {
        super(principalsHandler, authenticationManager);
        this.maxHistory = maxHistory;
        this.tableHandlers = tableHandlers;
    }

    public StatusCode run() {
        StatusCode result = StatusCode.SUCCESS;

        for (CrudDatabaseHandler<?, ?> h : tableHandlers) {
            result = result.bind(() -> h.cleanDataOlderThan(maxHistory));
        }

        return result;
    }
}

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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Update;
import fj.data.Validation;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

import static de.njsm.stocks.server.v2.db.jooq.Tables.UPDATES;

@Repository
@RequestScope
public class UpdateBackend extends FailSafeDatabaseHandler {

    public UpdateBackend(ConnectionFactory connectionFactory) {
        super(connectionFactory);
    }

    public Validation<StatusCode, List<Update>> get() {
        return runFunction(context -> {
            List<Update> dbResult = context
                    .selectFrom(UPDATES)
                    .fetch(record -> Update.builder()
                            .table(record.getTableName())
                            .lastUpdate(record.getLastUpdate().toInstant())
                            .build());
            return Validation.success(dbResult);
        });
    }
}

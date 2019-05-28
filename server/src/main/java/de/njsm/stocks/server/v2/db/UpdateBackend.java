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

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Update;
import fj.data.Validation;

import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.db.jooq.Tables.UPDATES;

public class UpdateBackend extends FailSafeDatabaseHandler {

    public UpdateBackend(ConnectionFactory connectionFactory,
                         String resourceIdentifier,
                         int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public Validation<StatusCode, List<Update>> getUpdates() {
        return runFunction(context -> {
            List<Update> dbResult = context.selectFrom(UPDATES)
                    .fetch()
                    .stream()
                    .map(record -> new Update(
                            record.getTableName(),
                            record.getLastUpdate().toInstant()))
                    .collect(Collectors.toList());
            return Validation.success(dbResult);
        });
    }
}

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

import de.njsm.stocks.server.v2.business.data.BitemporalUser;
import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.business.data.UserForGetting;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;

public class UserHandler extends CrudDatabaseHandler<UserRecord, User> {


    public UserHandler(ConnectionFactory connectionFactory,
                       String resourceIdentifier,
                       int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    @Override
    protected Table<UserRecord> getTable() {
        return USER;
    }

    @Override
    protected Function<UserRecord, User> getDtoMap(boolean bitemporal) {
        if (bitemporal)
            return cursor -> new BitemporalUser(
                    cursor.getId(),
                    cursor.getVersion(),
                    cursor.getValidTimeStart().toInstant(),
                    cursor.getValidTimeEnd().toInstant(),
                    cursor.getTransactionTimeStart().toInstant(),
                    cursor.getTransactionTimeEnd().toInstant(),
                    cursor.getInitiates(),
                    cursor.getName()
            );
        else
            return record -> new UserForGetting(
                    record.getId(),
                    record.getVersion(),
                    record.getName()
            );
    }

    @Override
    protected TableField<UserRecord, Integer> getIdField() {
        return USER.ID;
    }

    @Override
    protected TableField<UserRecord, Integer> getVersionField() {
        return USER.VERSION;
    }

    @Override
    protected List<Field<?>> getNontemporalFields() {
        return Arrays.asList(
                USER.ID,
                USER.VERSION,
                USER.NAME
        );
    }
}

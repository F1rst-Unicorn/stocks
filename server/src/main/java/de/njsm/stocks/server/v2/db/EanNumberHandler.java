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

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import org.jooq.Table;
import org.jooq.TableField;

import java.sql.Connection;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.EAN_NUMBER;


public class EanNumberHandler extends CrudDatabaseHandler<EanNumberRecord, EanNumber> {


    public EanNumberHandler(Connection connection,
                            String resourceIdentifier,
                            int timeout,
                            InsertVisitor<EanNumberRecord> visitor) {
        super(connection, resourceIdentifier, timeout, visitor);
    }

    @Override
    protected Table<EanNumberRecord> getTable() {
        return EAN_NUMBER;
    }

    @Override
    protected TableField<EanNumberRecord, Integer> getIdField() {
        return EAN_NUMBER.ID;
    }

    @Override
    protected TableField<EanNumberRecord, Integer> getVersionField() {
        return EAN_NUMBER.VERSION;
    }

    @Override
    protected Function<EanNumberRecord, EanNumber> getDtoMap() {
        return cursor -> new EanNumber(
                cursor.getId(),
                cursor.getVersion(),
                cursor.getNumber(),
                cursor.getIdentifies()
                );
    }

}

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

package de.njsm.stocks.server.v2.business.data;

import java.time.OffsetDateTime;

public abstract class VersionedData extends Data {

    public int version;

    public OffsetDateTime validTimeStart;

    public OffsetDateTime validTimeEnd;

    public OffsetDateTime transactionTimeStart;

    public OffsetDateTime transactionTimeEnd;

    public VersionedData() {
    }

    public VersionedData(int id, int version) {
        super(id);
        this.version = version;
    }

    public VersionedData(int id, int version, OffsetDateTime validTimeStart, OffsetDateTime validTimeEnd, OffsetDateTime transactionTimeStart, OffsetDateTime transactionTimeEnd) {
        super(id);
        this.version = version;
        this.validTimeStart = validTimeStart;
        this.validTimeEnd = validTimeEnd;
        this.transactionTimeStart = transactionTimeStart;
        this.transactionTimeEnd = transactionTimeEnd;
    }
}

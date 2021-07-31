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

package de.njsm.stocks.common.api.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.njsm.stocks.common.api.Bitemporal;
import de.njsm.stocks.common.api.UserDevice;

import java.time.Instant;
import java.util.Objects;

public class BitemporalUserDevice extends BitemporalData implements Bitemporal<UserDevice>, UserDevice {

    private final String name;

    private final int belongsTo;

    public BitemporalUserDevice(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, String name, int belongsTo) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.name = name;
        this.belongsTo = belongsTo;
    }

    @Override
    public String getName() {
        return name;
    }

    @JsonIgnore
    @Override
    public int getBelongsTo() {
        return belongsTo;
    }

    /**
     * JSON property name. Keep for backward compatibility
     */
    public int getUserId() {
        return belongsTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BitemporalUserDevice that = (BitemporalUserDevice) o;
        return getBelongsTo() == that.getBelongsTo() && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getBelongsTo());
    }

    @Override
    public boolean isContainedIn(UserDevice item) {
        return Bitemporal.super.isContainedIn(item) &&
                name.equals(item.getName()) &&
                belongsTo == item.getBelongsTo();
    }
}

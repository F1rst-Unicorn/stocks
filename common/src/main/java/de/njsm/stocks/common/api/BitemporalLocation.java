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

package de.njsm.stocks.common.api;

import java.time.Instant;
import java.util.Objects;

public class BitemporalLocation extends BitemporalData implements Bitemporal<Location>, Location {

    private final String name;

    private final String description;

    public BitemporalLocation(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, String name, String description) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.name = name;
        this.description = description;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        BitemporalLocation that = (BitemporalLocation) o;
        return getName().equals(that.getName()) && getDescription().equals(that.getDescription());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getName(), getDescription());
    }

    @Override
    public boolean isContainedIn(Location item) {
        return Bitemporal.super.isContainedIn(item) &&
                name.equals(item.getName()) &&
                description.equals(item.getDescription());
    }
}

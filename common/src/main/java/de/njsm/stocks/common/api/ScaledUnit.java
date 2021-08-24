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

import com.fasterxml.jackson.annotation.JsonGetter;
import com.google.common.base.Preconditions;

import java.math.BigDecimal;

import static java.util.Objects.requireNonNullElse;

public interface ScaledUnit extends Entity<ScaledUnit> {

    @JsonGetter
    BigDecimal scale();

    @JsonGetter
    int unit();

    interface Builder<T> {

        T scale(BigDecimal v);

        T unit(int v);
    }

    interface ScaleFromString<T> {

        T scale(BigDecimal v);

        default T scale(String v) {
            try {
                return scale(new BigDecimal(requireNonNullElse(v, "not a number")));
            } catch (NumberFormatException e) {
                throw new IllegalStateException("scale not a java.math.BigDecimal: " + v, e);
            }
        }
    }

    @Override
    default boolean isContainedIn(ScaledUnit item, boolean increment) {
        return Entity.super.isContainedIn(item, increment) &&
                scale().equals(item.scale()) &&
                unit() == item.unit();
    }

    @Override
    default void validate() {
        Entity.super.validate();
        Preconditions.checkState(unit() > 0, "unit id is invalid");
    }
}

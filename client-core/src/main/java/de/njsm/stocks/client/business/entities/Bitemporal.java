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

package de.njsm.stocks.client.business.entities;

import com.google.common.base.Preconditions;

import java.time.Instant;

public interface Bitemporal<T extends Entity<T>> extends Versionable<T> {

    Instant validTimeStart();

    Instant validTimeEnd();

    Instant transactionTimeStart();

    Instant transactionTimeEnd();

    int initiates();

    @Override
    default boolean isContainedIn(T item, boolean increment) {
        if (item instanceof Bitemporal) {
            Bitemporal<T> castedItem = (Bitemporal) item;
            return Versionable.super.isContainedIn(item, increment) &&
                    validTimeStart().equals(castedItem.validTimeStart()) &&
                    validTimeEnd().equals(castedItem.validTimeEnd()) &&
                    transactionTimeStart().equals(castedItem.transactionTimeStart()) &&
                    transactionTimeEnd().equals(castedItem.transactionTimeEnd());
        } else {
            return false;
        }
    }

    @Override
    default void validate() {
        Versionable.super.validate();
        Preconditions.checkState(initiates() > 0, "initiates id is invalid");
    }

    interface Builder<T> extends Versionable.Builder<T> {

        T validTimeStart(Instant v);

        T validTimeEnd(Instant v);

        T transactionTimeStart(Instant v);

        T transactionTimeEnd(Instant v);

        T initiates(int v);
    }
}

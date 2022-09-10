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

import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.time.Period;
import java.util.Optional;

@AutoValue
public abstract class FoodEditErrorDetails implements Versionable<Food>, ErrorDetails {

    public abstract String name();

    public abstract Period expirationOffset();

    public abstract Optional<Integer> location();

    public abstract int storeUnit();

    public abstract String description();

    public static FoodEditErrorDetails create(int id, int version, String name, Period expirationOffset, @Nullable Integer location, int storeUnit, String description) {
        return new AutoValue_FoodEditErrorDetails(id, version, name, expirationOffset, Optional.ofNullable(location), storeUnit, description);
    }

    public static FoodEditErrorDetails create(int id, int version, String name, Period expirationOffset, Optional<Integer> location, int storeUnit, String description) {
        return new AutoValue_FoodEditErrorDetails(id, version, name, expirationOffset, location, storeUnit, description);
    }

    @Override
    public <I, O> O accept(ErrorDetailsVisitor<I, O> visitor, I input) {
        return visitor.foodEditErrorDetails(this, input);
    }
}

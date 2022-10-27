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

import java.util.List;

import static java.util.Collections.emptyList;

@AutoValue
public abstract class ListWithSuggestion<T> {

    public abstract List<T> list();

    public abstract int suggestion();

    public T suggested() {
        return list().get(suggestion());
    }

    public boolean isEmpty() {
        return list().isEmpty();
    }

    public static <T> ListWithSuggestion<T> create(List<T> list, int suggestion) {
        if (list.isEmpty() && suggestion == 0)
            return empty();
        if (suggestion < 0 || list.size() <= suggestion)
            throw new IndexOutOfBoundsException("index " + suggestion + ", size " + list.size());
        return new AutoValue_ListWithSuggestion<>(list, suggestion);
    }

    public static <T> ListWithSuggestion<T> empty() {
        return new AutoValue_ListWithSuggestion<>(emptyList(), 0);
    }
}

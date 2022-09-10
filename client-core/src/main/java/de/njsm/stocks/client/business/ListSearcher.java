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

package de.njsm.stocks.client.business;

import de.njsm.stocks.client.business.entities.Entity;
import de.njsm.stocks.client.business.entities.Identifiable;
import de.njsm.stocks.client.business.entities.conflict.ConflictData;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ListSearcher {

    private static <E extends Entity<E>, T extends Identifiable<E>>
    Optional<Integer> searchFirst(List<T> list, int id) {
        return searchFirst(list, v -> v.id() == id);
    }

    private static <T> Optional<Integer> searchFirst(List<T> list, Predicate<T> predicate) {
        int i = 0;
        for (T item : list) {
            if (predicate.test(item))
                return Optional.of(i);
            i++;
        }
        return Optional.empty();
    }

    public static <E extends Entity<E>, I extends Identifiable<E>>
    Optional<Integer> searchFirstOptional(List<? extends I> list,
                                          ConflictData<? extends Optional<? extends I>> key) {
        int mappedPosition = or(key.suggestedValue().flatMap(v -> searchFirst(list, v.id())),
                () -> or(key.local().flatMap(v -> searchFirst(list, v.id())),
                () -> or(key.remote().flatMap(v -> searchFirst(list, v.id())),
                () -> key.original().flatMap(v -> searchFirst(list, v.id())))))
                .orElse(-1);
        return Optional.of(mappedPosition)
                .filter(v -> v != -1);
    }

    public static <E extends Entity<E>, I extends Identifiable<E>>
    Optional<Integer> searchFirst(List<? extends I> list, ConflictData<? extends I> key) {
        int mappedPosition = or(searchFirst(list, key.suggestedValue().id()),
                () -> or(searchFirst(list, key.local().id()),
                () -> or(searchFirst(list, key.remote().id()),
                () -> searchFirst(list, key.original().id()))))
                .orElse(-1);
        return Optional.of(mappedPosition)
                .filter(v -> v != -1);
    }

    public static <E extends Entity<E>, T extends Identifiable<E>>
    int findFirst(List<T> list, int id) {
        return searchFirst(list, v -> v.id() == id).orElseThrow(() -> new IllegalStateException("No matching item found"));
    }

    // Optional.or() is Java API 9 only
    private static <T> Optional<T> or(Optional<T> source, Supplier<? extends Optional<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (source.isPresent()) {
            return source;
        } else {
            @SuppressWarnings("unchecked")
            Optional<T> r = (Optional<T>) supplier.get();
            return Objects.requireNonNull(r);
        }
    }
}

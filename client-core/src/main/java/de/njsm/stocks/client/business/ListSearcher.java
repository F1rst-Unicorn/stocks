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
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.ListWithSuggestion;
import de.njsm.stocks.client.business.entities.conflict.ConflictData;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class ListSearcher {

    public static <E extends Entity<E>, I extends Id<E>>
    Optional<Integer> searchFirstOptional(List<? extends I> list,
                                          ConflictData<? extends Optional<? extends I>> key) {
        return key.suggestedValue().flatMap(v -> searchFirst(list, v.id()))
                .or(() -> key.local().flatMap(v -> searchFirst(list, v.id())))
                .or(() -> key.remote().flatMap(v -> searchFirst(list, v.id())))
                .or(() -> key.original().flatMap(v -> searchFirst(list, v.id())));
    }

    public static <E extends Entity<E>, T extends Id<E>>
    Optional<Integer> searchFirst(List<T> list, int id) {
        return searchFirst(list, v -> v.id() == id);
    }

    public static <E extends Entity<E>, T extends Id<E>>
    Optional<Integer> searchFirst(List<T> list, Id<E> id) {
        return searchFirst(list, v -> v.id() == id.id());
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

    public static <E extends Entity<E>, I extends Id<E>>
    Optional<Integer> searchFirst(List<? extends I> list, ConflictData<? extends I> key) {
        return searchFirst(list, key.suggestedValue().id())
                .or(() -> searchFirst(list, key.local().id()))
                .or(() -> searchFirst(list, key.remote().id()))
                .or(() -> searchFirst(list, key.original().id()));
    }

    public static <E extends Entity<E>, T extends Id<E>>
    ListWithSuggestion<T> findFirstSuggestion(List<T> list, Id<E> id) {
        return ListWithSuggestion.create(list, findFirst(list, id));
    }

    public static <E extends Entity<E>, T extends Id<E>>
    int findFirst(List<T> list, int id) {
        return searchFirst(list, v -> v.id() == id).orElseThrow(() -> new IllegalStateException("No matching item " + id + " found: " + list));
    }

    public static <E extends Entity<E>, T extends Id<E>>
    int findFirst(List<T> list, Id<E> id) {
        return searchFirst(list, v -> v.id() == id.id()).orElseThrow(() -> new IllegalStateException("No matching item " + id.id() + " found: " + list));
    }

    public static <E extends Entity<E>, T extends Id<E>>
    ListWithSuggestion<T> searchFirstSuggested(List<T> list, Id<E> id) {
        return ListWithSuggestion.create(list, searchFirst(list, v -> v.id() == id.id()).orElse(0));
    }
}

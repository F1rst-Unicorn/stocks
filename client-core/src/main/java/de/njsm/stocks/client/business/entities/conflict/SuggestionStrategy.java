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

package de.njsm.stocks.client.business.entities.conflict;


import static de.njsm.stocks.client.business.entities.conflict.ConflictData.FieldComparison.BOTH_DIFFER;

public abstract class SuggestionStrategy<T> {

    public static <T> SuggestionStrategy<T> select() {
        return new SelectingSuggestionStrategy<>();
    }

    public static SuggestionStrategy<String> merge() {
        return new MergingSuggestionStrategy(select());
    }

    public abstract T suggest(ConflictData<T> data);

    private static class SelectingSuggestionStrategy<T> extends SuggestionStrategy<T> {
        @Override
        public T suggest(ConflictData<T> data) {
            switch (data.compare()) {
                case EQUAL:
                case LOCAL_DIFFERS:
                case BOTH_DIFFER:
                case BOTH_DIFFER_EQUALLY:
                    return data.local();
                default:
                    return data.remote();
            }
        }
    }

    private static class MergingSuggestionStrategy extends SuggestionStrategy<String> {

        private final SuggestionStrategy<String> fallback;

        public MergingSuggestionStrategy(SuggestionStrategy<String> fallback) {
            this.fallback = fallback;
        }

        @Override
        public String suggest(ConflictData<String> data) {
            if (data.compare() == BOTH_DIFFER) {
                return String.format("%%s:\n%s\n\n%%s:\n%s\n\n%%s:\n%s",
                        data.original(),
                        data.remote(),
                        data.local());
            } else {
                return fallback.suggest(data);
            }
        }
    }
}

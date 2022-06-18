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

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;

import static de.njsm.stocks.client.business.entities.conflict.ConflictData.FieldComparison.*;
import static de.njsm.stocks.client.business.entities.conflict.SuggestionStrategy.merge;
import static de.njsm.stocks.client.business.entities.conflict.SuggestionStrategy.select;

/**
 * Represent three versions of a datum which results from two conflicting
 * edits of an entity
 */
@AutoValue
public abstract class ConflictData<T> {

    public static <T> ConflictData<T> create(T original, T remote, T local) {
        return new AutoValue_ConflictData<>(original, remote, local, select());
    }

    public static ConflictData<String> createMerging(String original, String remote, String local) {
        return new AutoValue_ConflictData<>(original, remote, local, merge());
    }

    /**
     * The datum assumed to be valid when the local edit occurred
     */
    public abstract T original();

    /**
     * The datum that resulted from the other, remote program's edit of the
     * original datum.
     */
    public abstract T remote();

    /**
     * The datum as it was entered by this program for the local edit that
     * caused the conflict.
     *
     * It has never been valid.
     */
    public abstract T local();

    abstract SuggestionStrategy<T> suggestionStrategy();

    public T suggestedValue() {
        return suggestionStrategy().suggest(this);
    }

    public boolean needsHandling() {
        return compare() == BOTH_DIFFER;
    }

    @Memoized
    FieldComparison compare() {
        if (remote().equals(local()))
            if (original().equals(remote()))
                return EQUAL;
            else
                return BOTH_DIFFER_EQUALLY;
        else if (remote().equals(original()))
            return LOCAL_DIFFERS;
        else if (local().equals(original()))
            return REMOTE_DIFFERS;
        else
            return BOTH_DIFFER;
    }

    enum FieldComparison {
        EQUAL,
        REMOTE_DIFFERS,
        LOCAL_DIFFERS,
        BOTH_DIFFER,
        BOTH_DIFFER_EQUALLY
    }
}

/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.business.data.conflict;

public enum FieldComparison {

    EQUAL,

    REMOTE_DIFFERS,

    LOCAL_DIFFERS,

    BOTH_DIFFER,

    BOTH_DIFFER_EQUALLY;

    public static <T> FieldComparison compare(T original, T remote, T local) {
        if (remote.equals(local))
            if (original.equals(remote))
                return EQUAL;
            else
                return BOTH_DIFFER_EQUALLY;
        else if (remote.equals(original))
            return LOCAL_DIFFERS;
        else if (local.equals(original))
            return REMOTE_DIFFERS;
        else
            return BOTH_DIFFER;
    }

    public static <T> T getValue(T original, T remote, T local) {
        return getValue(compare(original, remote, local), remote, local);
    }

    public static <T> T getValue(FieldComparison comparison, T remote, T local) {
        switch (comparison) {
            case EQUAL:
            case LOCAL_DIFFERS:
            case BOTH_DIFFER:
            case BOTH_DIFFER_EQUALLY:
                return local;
            default:
                return remote;
        }
    }
}

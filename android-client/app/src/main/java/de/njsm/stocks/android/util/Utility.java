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

package de.njsm.stocks.android.util;

import androidx.annotation.Nullable;
import de.njsm.stocks.android.db.entities.Data;

import java.util.List;
import java.util.Optional;

public class Utility {

    public static <T extends Data> Optional<Integer> find(int id, @Nullable List<T> list) {
        if (list == null)
            return Optional.empty();

        int position = 0;
        for (T l : list) {
            if (l.id == id) {
                return Optional.of(position);
            }
            position++;
        }
        return Optional.empty();
    }
}

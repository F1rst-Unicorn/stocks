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

import de.njsm.stocks.R;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class MergingTextFieldComparator extends FieldComparator<String> {

    public MergingTextFieldComparator(String original, String remote, String local) {
        super(original, remote, local);
    }

    public boolean compare(Function<Integer, String> stringProvider, BiConsumer<String, Boolean> consumer) {
        FieldComparison comparison = FieldComparison.compare(original, remote, local);
        if (comparison.equals(FieldComparison.BOTH_DIFFER)) {
            String newText = String.format("%s:\n%s\n\n%s:\n%s\n\n%s:\n%s",
                    stringProvider.apply(R.string.hint_original),
                    original,
                    stringProvider.apply(R.string.hint_remote),
                    remote,
                    stringProvider.apply(R.string.hint_local),
                    local);
            consumer.accept(newText, true);
            return true;
        } else {
            consumer.accept(FieldComparison.getValue(comparison, remote, local), false);
            return false;
        }
    }
}

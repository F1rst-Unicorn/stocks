/* stocks is client-server program to manage a household's food stock
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
 */

package de.njsm.stocks.android.util.paging;

import java.util.Arrays;

public class Key {

    private int[] positions;

    private int keyIndex;

    public Key(int dimensions) {
        positions = new int[dimensions];
    }

    public int getPosition() {
        return Arrays.stream(positions).sum();
    }

    public int getIndex(int position) {
        return positions[position];
    }

    public void increment(int position) {
        positions[position]++;
    }

    public void decrement(int position) {
        positions[position]--;
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public void setKeyIndex(int keyIndex) {
        this.keyIndex = keyIndex;
    }

    public Key copy() {
        Key result = new Key(positions.length);
        result.keyIndex = keyIndex;
        result.positions = Arrays.copyOf(positions, positions.length);
        return result;
    }

    public int getPartialPosition() {
        return positions[keyIndex];
    }
}

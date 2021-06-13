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

package de.njsm.stocks.android.business.data.eventlog;

public class SentenceObject {

    private final String explicitName;

    private final String implicitName;

    private final String explicitNameGenitive;

    private String current;

    public SentenceObject(String explicitName, String implicitName, String explicitNameGenitive) {
        this.explicitName = explicitName;
        this.implicitName = implicitName;
        this.explicitNameGenitive = explicitNameGenitive;
        this.current = explicitName;
    }

    public String get() {
        String result = current;
        current = implicitName;
        return result;
    }

    public String getGenitive() {
        if (current == explicitName) {
            current = implicitName;
            return " " + explicitNameGenitive;
        } else {
            return "";
        }
    }
}

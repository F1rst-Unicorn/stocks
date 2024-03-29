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

package de.njsm.stocks.client.databind.event;

/**
 * The grammatical accusative object of a sentence. When getting it for the
 * first time, it returns an explicit description, while further getting returns
 * only a pronoun. E.g. "Jack moved A BANANA and ate half of IT". Providing the
 * all-caps words is the job of this class.
 */
class SentenceObject {

    private final String explicitName;

    private final String implicitName;

    private final String explicitNameGenitive;

    private String current;

    SentenceObject(String explicitName, String implicitName, String explicitNameGenitive) {
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

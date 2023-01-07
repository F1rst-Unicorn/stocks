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

import de.njsm.stocks.client.business.entities.event.EditedField;

import java.util.function.Consumer;
import java.util.function.Function;

abstract class PartialDiffGenerator<T> {

    private final Function<Integer, String> dictionary;

    private final EditedField<T> editedField;

    private final SentenceObject object;

    PartialDiffGenerator(Function<Integer, String> dictionary, EditedField<T> editedField, SentenceObject object) {
        this.dictionary = dictionary;
        this.editedField = editedField;
        this.object = object;
    }

    abstract int getStringId();

    EditedField<T> get() {
        return editedField;
    }

    abstract Object[] getFormatArguments();

    void generate(Consumer<String> consumer) {
        if (editedField.changed()) {
            String template = dictionary.apply(getStringId());
            String description = String.format(template, getFormatArguments());
            consumer.accept(description);
        }
    }

    SentenceObject getObject() {
        return object;
    }
}

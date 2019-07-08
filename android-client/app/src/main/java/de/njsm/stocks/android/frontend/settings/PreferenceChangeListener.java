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

package de.njsm.stocks.android.frontend.settings;

import androidx.preference.Preference;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PreferenceChangeListener<T> implements Preference.OnPreferenceChangeListener {

    private String key;

    private BiConsumer<String, T> callback;

    private Function<String, T> transformer;

    public PreferenceChangeListener(String key, BiConsumer<String, T> callback, Function<String, T> transformer) {
        this.key = key;
        this.callback = callback;
        this.transformer = transformer;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (newValue instanceof String) {
            callback.accept(key, transformer.apply((String) newValue));
            preference.setSummary(String.valueOf(newValue));
        }
        return true;
    }
}

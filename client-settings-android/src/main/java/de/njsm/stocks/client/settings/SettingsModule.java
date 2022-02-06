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

package de.njsm.stocks.client.settings;

import android.content.Context;
import android.content.SharedPreferences;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import de.njsm.stocks.client.business.Settings;
import de.njsm.stocks.client.business.SettingsWriter;

@Module
public abstract class SettingsModule {

    private static final String PREFERENCES_FILE_NAME = "stocks_prefs";

    @Provides
    public static SettingsImpl settingsImpl(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
        return new SettingsImpl(sharedPreferences);
    }

    @Binds
    public abstract Settings settings(SettingsImpl impl);

    @Binds
    public abstract SettingsWriter settingsWriter(SettingsImpl impl);
}

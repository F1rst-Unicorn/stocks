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

package de.njsm.stocks.android.util;

import android.util.Log;

public class Logger {

    private Class<?> tag;

    public Logger(Class<?> tag) {
        this.tag = tag;
    }

    public void v(String message, Throwable t) {
        Log.v(tag.getCanonicalName(), message, t);
    }

    public void v(String message) {
        Log.v(tag.getCanonicalName(), message);
    }

    public void d(String message, Throwable t) {
        Log.d(tag.getCanonicalName(), message, t);
    }

    public void d(String message) {
        Log.d(tag.getCanonicalName(), message);
    }

    public void i(String message, Throwable t) {
        Log.i(tag.getCanonicalName(), message, t);
    }

    public void i(String message) {
        Log.i(tag.getCanonicalName(), message);
    }

    public void w(String message, Throwable t) {
        Log.w(tag.getCanonicalName(), message, t);
    }

    public void w(String message) {
        Log.w(tag.getCanonicalName(), message);
    }

    public void e(String message, Throwable t) {
        Log.e(tag.getCanonicalName(), message, t);
    }

    public void e(String message) {
        Log.e(tag.getCanonicalName(), message);
    }
}

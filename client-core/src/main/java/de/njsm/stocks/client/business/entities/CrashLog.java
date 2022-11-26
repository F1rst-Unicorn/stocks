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

package de.njsm.stocks.client.business.entities;

import com.google.auto.value.AutoValue;

import java.time.LocalDateTime;

@AutoValue
public abstract class CrashLog {

    public abstract String fileName();

    public abstract LocalDateTime timeOccurred();

    public abstract String exceptionName();

    public abstract String stackTrace();

    public static CrashLog create(String fileName, LocalDateTime timeOccurred, String exceptionName, String stackTrace) {
        return new AutoValue_CrashLog(fileName, timeOccurred, exceptionName, stackTrace);
    }

    public String renderedContent() {
        return String.format("%s\n%s\n%s",
                exceptionName(),
                timeOccurred().toString(),
                stackTrace());
    }
}

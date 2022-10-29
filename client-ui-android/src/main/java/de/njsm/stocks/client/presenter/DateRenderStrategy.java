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

package de.njsm.stocks.client.presenter;

import android.text.format.DateUtils;
import de.njsm.stocks.client.business.Localiser;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateRenderStrategy {

    private static final java.time.format.DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final Localiser localiser;

    @Inject
    public DateRenderStrategy(Localiser localiser) {
        this.localiser = localiser;
    }

    public String render(LocalDate date) {
        return FORMAT.format(date);
    }

    public CharSequence renderRelative(LocalDate date) {
        return DateUtils.getRelativeTimeSpanString(
                localiser.toInstant(date).toEpochMilli(),
                localiser.epochMilli(),
                0L, DateUtils.FORMAT_ABBREV_ALL);
    }
}

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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateRenderStrategy {

    private static final java.time.format.DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final java.time.format.DateTimeFormatter FORMAT_SHORT = DateTimeFormatter.ofPattern("dd.MM.yy");
    private static final java.time.format.DateTimeFormatter FORMAT_WITH_TIME = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final Localiser localiser;

    @Inject
    public DateRenderStrategy(Localiser localiser) {
        this.localiser = localiser;
    }

    public String render(LocalDate date) {
        return render(date, FORMAT);
    }

    public String render(LocalDateTime date) {
        return FORMAT_WITH_TIME.format(date);
    }

    public String renderEpochSeconds(float value) {
        Instant i = Instant.ofEpochSecond((long) value);
        i = i.minusSeconds(ZoneId.systemDefault().getRules().getOffset(i).getTotalSeconds());
        return render(localiser.toLocalDate(i), FORMAT_SHORT);
    }

    private String render(LocalDate localDate, DateTimeFormatter format) {
        return format.format(localDate);
    }

    public CharSequence renderRelative(LocalDate date) {
        return DateUtils.getRelativeTimeSpanString(
                localiser.toInstant(date).toEpochMilli(),
                localiser.epochMilli(),
                0L, DateUtils.FORMAT_ABBREV_ALL);
    }

    public float toFloat(LocalDateTime time) {
        var value = localiser.toInstant(time);
        return value.getEpochSecond() + ZoneId.systemDefault().getRules().getOffset(value).getTotalSeconds();
    }
}

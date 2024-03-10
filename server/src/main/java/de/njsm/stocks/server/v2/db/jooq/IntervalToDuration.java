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

package de.njsm.stocks.server.v2.db.jooq;

import org.jooq.Converter;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;
import org.jooq.types.YearToSecond;

import java.time.Duration;

/**
 * Only supports `INTERVAL DAY HOUR MINUTE SECOND` type
 */
public class IntervalToDuration implements Converter<YearToSecond, Duration> {

    @Override
    public Duration from(YearToSecond yearToSecond) {
        return Duration.ZERO
                .plusDays(yearToSecond.getDays())
                .plusHours(yearToSecond.getHours())
                .plusMinutes(yearToSecond.getMinutes())
                .plusSeconds(yearToSecond.getSeconds());
    }

    @Override
    public YearToSecond to(Duration duration) {
        return new YearToSecond(
                new YearToMonth(),
                new DayToSecond(
                        (int) duration.toDaysPart(),
                        duration.toHoursPart(),
                        duration.toMinutesPart(),
                        duration.toSecondsPart())
        );
    }

    @Override
    public Class<YearToSecond> fromType() {
        return YearToSecond.class;
    }

    @Override
    public Class<Duration> toType() {
        return Duration.class;
    }
}

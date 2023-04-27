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

package de.njsm.stocks.client.business;

import javax.inject.Inject;
import java.time.*;

public class Localiser {

    private final Clock clock;

    @Inject
    public Localiser(Clock clock) {
        this.clock = clock;
    }

    public Instant toInstant(LocalDate d) {
        return d.atStartOfDay(ZoneId.systemDefault()).toInstant();
    }

    public Instant toInstant(LocalDateTime d) {
        return d.atZone(ZoneId.systemDefault()).toInstant();
    }

    public Instant toUtcInstant(LocalDate d) {
        return d.atStartOfDay(ZoneId.of("UTC")).toInstant();
    }

    public LocalDate toLocalDate(Instant i) {
        return i.atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public LocalDateTime toLocalDateTime(Instant i) {
        return i.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public LocalDate today() {
        return toLocalDate(clock.get());
    }

    public LocalDateTime now() {
        return toLocalDateTime(clock.get());
    }

    public long epochMilli() {
        return clock.get().toEpochMilli();
    }
}

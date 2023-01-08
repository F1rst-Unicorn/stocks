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

package de.njsm.stocks.client.business.event;

import com.google.auto.value.AutoValue;
import de.njsm.stocks.client.business.entities.event.ActivityEvent;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@AutoValue
public abstract class ActivityEventPage {

    public abstract List<ActivityEvent> events();

    public abstract Optional<LocalDate> previousPageKey();

    public abstract Optional<LocalDate> nextPageKey();

    public static ActivityEventPage create(List<ActivityEvent> events, Optional<LocalDate> previous, Optional<LocalDate> next) {
        return new AutoValue_ActivityEventPage(events, previous,  next);
    }
}

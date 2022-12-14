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

package de.njsm.stocks.client.database;

import androidx.room.TypeConverter;
import de.njsm.stocks.client.business.entities.Id;
import de.njsm.stocks.client.business.entities.IdImpl;
import de.njsm.stocks.client.business.entities.Location;
import de.njsm.stocks.client.business.entities.Unit;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.Period;

import static de.njsm.stocks.client.business.Constants.INFINITY;
import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_DATE_FORMAT;
import static de.njsm.stocks.client.database.StocksDatabase.DATABASE_INFINITY;

public class TypeConverters {

    @TypeConverter
    public String instantToDb(Instant instant) {
        if (instant.equals(INFINITY)) {
            return DATABASE_DATE_FORMAT.format(DATABASE_INFINITY);
        } else {
            return DATABASE_DATE_FORMAT.format(instant);
        }
    }

    @TypeConverter
    public Instant dbToInstant(String rawInstant) {
        if (rawInstant == null)
            return null;

        Instant result = DATABASE_DATE_FORMAT.parse(rawInstant, Instant::from);
        if (result.equals(DATABASE_INFINITY)) {
            return INFINITY;
        } else {
            return result;
        }
    }

    @TypeConverter
    public String bigDecimalToDb(BigDecimal bigDecimal) {
        return bigDecimal.toString();
    }

    @TypeConverter
    public BigDecimal dbToBigDecimal(String rawBigDecimal) {
        return new BigDecimal(rawBigDecimal);
    }

    @TypeConverter
    public String durationToDb(Duration d) {
        return d.toString();
    }

    @TypeConverter
    public Duration dbToDuration(String rawDuration) {
        return Duration.parse(rawDuration);
    }

    @TypeConverter
    public Period dbToPeriod(int periodInDays) {
        return Period.ofDays(periodInDays);
    }

    @TypeConverter
    public int periodToDb(Period period) {
        return period.getDays();
    }

    @TypeConverter
    public Id<Location> locationId(int id) {
        return IdImpl.create(id);
    }

    @TypeConverter
    public Id<Unit> unitId(int id) {
        return IdImpl.create(id);
    }
}

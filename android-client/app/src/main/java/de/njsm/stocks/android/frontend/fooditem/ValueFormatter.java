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

package de.njsm.stocks.android.frontend.fooditem;

import com.github.mikephil.charting.components.AxisBase;

import org.threeten.bp.Instant;
import org.threeten.bp.ZoneId;

import de.njsm.stocks.android.util.Config;

public class ValueFormatter extends com.github.mikephil.charting.formatter.ValueFormatter {

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        Instant i = Instant.ofEpochSecond((long) value);
        i = i.minusSeconds(ZoneId.systemDefault().getRules().getOffset(i).getTotalSeconds());
        return Config.PRETTY_DATE_FORMAT.format(i);
    }
}

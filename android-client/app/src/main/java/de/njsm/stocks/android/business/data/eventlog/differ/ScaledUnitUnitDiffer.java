/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.business.data.eventlog.differ;

import de.njsm.stocks.R;
import de.njsm.stocks.android.business.data.eventlog.PartialDiffGenerator;
import de.njsm.stocks.android.business.data.eventlog.SentenceObject;
import de.njsm.stocks.android.db.views.ScaledUnitView;

import java.util.function.IntFunction;

public class ScaledUnitUnitDiffer extends PartialDiffGenerator<ScaledUnitView> {

    public ScaledUnitUnitDiffer(IntFunction<String> stringResourceResolver, ScaledUnitView oldEntity, ScaledUnitView newEntity, SentenceObject object) {
        super(stringResourceResolver, oldEntity, newEntity, object);
    }

    @Override
    protected boolean entitiesDiffer() {
        return getOldEntity().getUnit() != getNewEntity().getUnit();
    }

    @Override
    protected int getStringId() {
        return R.string.event_scaled_unit_unit_changed;
    }

    @Override
    protected Object[] getFormatArguments() {
        return new Object[] {
                getObject().getGenitive(),
                getOldEntity().getUnitEntity().getName(),
                getNewEntity().getUnitEntity().getName()
        };
    }


}

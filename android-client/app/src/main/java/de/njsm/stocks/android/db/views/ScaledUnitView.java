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

package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.entities.Unit;
import org.threeten.bp.Instant;

import java.math.BigDecimal;

public class ScaledUnitView extends ScaledUnit {

    @Embedded(prefix = "unit_")
    Unit unitEntity;

    public ScaledUnitView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, @NonNull BigDecimal scale, int unitEntity, Unit unit1) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, scale, unitEntity);
        this.unitEntity = unit1;
    }

    public ScaledUnitView() {
        unitEntity = new Unit();
    }

    public Unit getUnitEntity() {
        return unitEntity;
    }

    public void setUnitEntity(Unit unitEntity) {
        this.unitEntity = unitEntity;
    }
}

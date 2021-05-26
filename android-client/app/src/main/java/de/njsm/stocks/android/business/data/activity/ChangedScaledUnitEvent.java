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

package de.njsm.stocks.android.business.data.activity;

import de.njsm.stocks.android.business.data.activity.differ.ScaledUnitScaleDiffer;
import de.njsm.stocks.android.business.data.activity.differ.ScaledUnitUnitDiffer;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.views.ScaledUnitView;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class ChangedScaledUnitEvent extends ChangedEntityEvent<ScaledUnitView> implements ScaledUnitIconResourceProvider {


    public ChangedScaledUnitEvent(User initiatorUser, UserDevice initiatorUserDevice, ScaledUnitView version1, ScaledUnitView version2) {
        super(initiatorUser, initiatorUserDevice, version1, version2);
    }

    @Override
    protected List<PartialDiffGenerator<ScaledUnitView>> getDiffers(IntFunction<String> stringResourceResolver, SentenceObject object) {
        List<PartialDiffGenerator<ScaledUnitView>> list = new ArrayList<>();
        list.add(new ScaledUnitScaleDiffer(stringResourceResolver, oldEntity, newEntity, object));
        list.add(new ScaledUnitUnitDiffer(stringResourceResolver, oldEntity, newEntity, object));
        return list;
    }

    @Override
    protected String getExplicitObject() {
        return oldEntity.getScale() + oldEntity.getUnitEntity().getAbbreviation();
    }

    @Override
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.changedScaledUnitEvent(arg);
    }
}

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

import java.util.function.IntFunction;

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.views.EanNumberView;

public class NewEanNumberEvent extends NewEntityEvent<EanNumberView> implements EanNumberIconResourceProvider {

    public NewEanNumberEvent(User initiatorUser, UserDevice initiatorDevice, EanNumberView entity) {
        super(initiatorUser, initiatorDevice, entity);
    }

    @Override
    public String describe(IntFunction<String> stringResourceResolver) {
        String template = stringResourceResolver.apply(R.string.event_eannumber_created);
        return String.format(template, initiatorUser.name, entity.eanCode, entity.getIdentifiedFoodName());
    }

    @Override
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.newEanNumberEvent(this, arg);
    }
}

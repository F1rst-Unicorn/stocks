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

import de.njsm.stocks.R;
import de.njsm.stocks.android.business.data.activity.differ.*;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.views.FoodWithLocationName;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntFunction;

public class ChangedFoodEvent extends ChangedEntityEvent<FoodWithLocationName> implements FoodIconResourceProvider {

    private final int eventIcon;

    public ChangedFoodEvent(User initiatorUser, UserDevice initiatorDevice, FoodWithLocationName oldEntity, FoodWithLocationName newEntity) {
        super(initiatorUser, initiatorDevice, oldEntity, newEntity);

        if (oldEntity.toBuy != newEntity.toBuy)
            if (newEntity.toBuy)
                eventIcon = R.drawable.baseline_add_shopping_cart_black_24;
            else
                eventIcon = R.drawable.baseline_remove_shopping_cart_black_24;
        else
            eventIcon = super.getEventIconResource();
    }

    @Override
    protected List<PartialDiffGenerator<FoodWithLocationName>> getDiffers(IntFunction<String> stringResourceResolver, SentenceObject object) {
        List<PartialDiffGenerator<FoodWithLocationName>> differs = new ArrayList<>();
        differs.add(new FoodRenameGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodNoMoreToBuyGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodToBuyGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodDefaultLocationSetGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodDefaultLocationUnsetGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodDefaultLocationChangedGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodExpirationOffsetChangedGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodDescriptionChangedGenerator(stringResourceResolver, oldEntity, newEntity, object));
        differs.add(new FoodStoreUnitChangedGenerator(stringResourceResolver, oldEntity, newEntity, object));
        return differs;
    }

    @Override
    protected String getExplicitObject() {
        return oldEntity.name;
    }

    @Override
    public int getEventIconResource() {
        return eventIcon;
    }

    @Override
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.changedFoodEvent(this, arg);
    }
}

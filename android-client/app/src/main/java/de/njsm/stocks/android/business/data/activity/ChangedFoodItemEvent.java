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
import de.njsm.stocks.android.db.views.FoodItemWithFoodNameView;
import de.njsm.stocks.android.util.Config;

public class ChangedFoodItemEvent extends ChangedEntityEvent<FoodItemWithFoodNameView> implements FoodItemIconResourceProvider {


    public ChangedFoodItemEvent(User initiatorUser, UserDevice initiatorDevice, FoodItemWithFoodNameView oldEntity, FoodItemWithFoodNameView newEntity) {
        super(initiatorUser, initiatorDevice, oldEntity, newEntity);
    }

    @Override
    public String describe(IntFunction<String> stringResourceResolver) {
        StringBuilder result = new StringBuilder();
        String template;
        String message;

        if (!oldEntity.getLocation().equals(newEntity.getLocation())) {
            template = stringResourceResolver.apply(R.string.event_food_item_changed_location);
            message = String.format(template,
                    initiatorUser.name,
                    oldEntity.getFoodName(),
                    oldEntity.getLocation(),
                    newEntity.getLocation());
            result.append(message);
        }

        if (!oldEntity.getEatByDate().equals(newEntity.getEatByDate())) {
            if (result.length() == 0) {
                template = stringResourceResolver.apply(R.string.event_food_item_changed_eat_by);
                message = String.format(template,
                        initiatorUser.name,
                        oldEntity.getFoodName(),
                        Config.PRETTY_DATE_FORMAT.format(oldEntity.getEatByDate()),
                        Config.PRETTY_DATE_FORMAT.format(newEntity.getEatByDate()));
            } else {
                template = " " + stringResourceResolver.apply(R.string.event_food_item_changed_eat_by_addendum);
                message = String.format(template,
                        Config.PRETTY_DATE_FORMAT.format(newEntity.getEatByDate()),
                        Config.PRETTY_DATE_FORMAT.format(oldEntity.getEatByDate()));
            }
            result.append(message);
        }

        return result.toString();
    }

    @Override
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.changedFoodItemEvent(this, arg);
    }
}

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
import de.njsm.stocks.android.db.views.FoodWithLocationName;

public class ChangedFoodEvent extends ChangedEntityEvent<FoodWithLocationName> implements FoodIconResourceProvider {

    private int eventIcon;

    public ChangedFoodEvent(FoodWithLocationName former, FoodWithLocationName current) {
        super(former, current);

        if (former.toBuy != current.toBuy)
            if (current.toBuy)
                eventIcon = R.drawable.baseline_add_shopping_cart_black_24;
            else
                eventIcon = R.drawable.baseline_remove_shopping_cart_black_24;
        else
            eventIcon = super.getEventIconResource();
    }

    @Override
    public String describe(IntFunction<String> stringResourceResolver) {
        StringBuilder result = new StringBuilder();
        String template;
        String description;
        String subject;

        if (!oldEntity.name.equals(newEntity.name)) {
            template = stringResourceResolver.apply(R.string.event_food_renamed);
            description = String.format(template, oldEntity.name, newEntity.name);
            result.append(description);
            result.append(" ");
        }

        if (oldEntity.toBuy && !newEntity.toBuy) {
            if (result.length() == 0) {
                subject = stringResourceResolver.apply(R.string.event_food_specific_name);
                subject = String.format(subject, oldEntity.name);
            } else
                subject = stringResourceResolver.apply(R.string.envent_generic_entity);
            template = stringResourceResolver.apply(R.string.event_food_to_buy_unset);
            description = String.format(template, subject);
            result.append(description);
            result.append(" ");
        }

        if (!oldEntity.toBuy && newEntity.toBuy) {
            if (result.length() == 0) {
                subject = stringResourceResolver.apply(R.string.event_food_specific_name);
                subject = String.format(subject, oldEntity.name);
            } else
                subject = stringResourceResolver.apply(R.string.envent_generic_entity);
            template = stringResourceResolver.apply(R.string.event_food_to_buy_set);
            description = String.format(template, subject);
            result.append(description);
            result.append(" ");
        }

        if (oldEntity.location == 0 && newEntity.location != 0) {
            if (result.length() == 0) {
                subject = stringResourceResolver.apply(R.string.event_food_specific_name_of) + " ";
                subject = String.format(subject, oldEntity.name);
            } else
                subject = "";
            template = stringResourceResolver.apply(R.string.event_food_location_set);
            description = String.format(template, subject, newEntity.locationName);
            result.append(description);
            result.append(" ");
        } else if (oldEntity.location != 0 && newEntity.location == 0) {
            if (result.length() == 0) {
                subject = stringResourceResolver.apply(R.string.event_food_specific_name_of) + " ";
                subject = String.format(subject, oldEntity.name);
            } else
                subject = "";
            template = stringResourceResolver.apply(R.string.event_food_location_unset);
            description = String.format(template, subject);
            result.append(description);
            result.append(" ");
        } else if (oldEntity.location != newEntity.location) {
            if (result.length() == 0) {
                subject = stringResourceResolver.apply(R.string.event_food_specific_name_of) + " ";
                subject = String.format(subject, oldEntity.name);
            } else
                subject = "";
            template = stringResourceResolver.apply(R.string.event_food_location_changed);
            description = String.format(template, subject, oldEntity.locationName, newEntity.locationName);
            result.append(description);
            result.append(" ");
        }

        if (oldEntity.expirationOffset != newEntity.expirationOffset) {
            if (result.length() == 0) {
                subject = stringResourceResolver.apply(R.string.event_food_specific_name_of) + " ";
                subject = String.format(subject, oldEntity.name);
            } else
                subject = "";
            template = stringResourceResolver.apply(R.string.event_food_expiration_offset_set);
            description = String.format(template, subject, newEntity.expirationOffset);
            result.append(description);
            result.append(" ");
        }

        return result.toString();
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

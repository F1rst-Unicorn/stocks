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

import java.util.ArrayList;
import java.util.function.IntFunction;

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;
import de.njsm.stocks.android.db.views.FoodWithLocationName;

public class ChangedFoodEvent extends ChangedEntityEvent<FoodWithLocationName> implements FoodIconResourceProvider {

    private int eventIcon;

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
    public String describe(IntFunction<String> stringResourceResolver) {
        String template;
        String description;
        ArrayList<String> partialSentences = new ArrayList<>();

        if (!oldEntity.name.equals(newEntity.name)) {
            template = stringResourceResolver.apply(R.string.event_food_renamed);
            description = String.format(template, initiatorUser.name, oldEntity.name, newEntity.name);
            partialSentences.add(description);
        }

        if (oldEntity.toBuy && !newEntity.toBuy) {
            if (partialSentences.size() == 0) {
                template = stringResourceResolver.apply(R.string.event_food_to_buy_unset);
                description = String.format(template, initiatorUser.name, oldEntity.name);
            } else {
                description = stringResourceResolver.apply(R.string.event_food_to_buy_unset_addendum);
            }
            partialSentences.add(description);
        }

        if (!oldEntity.toBuy && newEntity.toBuy) {
            if (partialSentences.size() == 0) {
                template = stringResourceResolver.apply(R.string.event_food_to_buy_set);
                description = String.format(template, initiatorUser.name, oldEntity.name);
            } else {
                description = stringResourceResolver.apply(R.string.event_food_to_buy_set_addendum);
            }
            partialSentences.add(description);
        }

        if (oldEntity.location == 0 && newEntity.location != 0) {
            if (partialSentences.size() == 0) {
                template = stringResourceResolver.apply(R.string.event_food_location_set);
                description = String.format(template, initiatorUser.name, oldEntity.name, newEntity.locationName);
            } else {
                template = stringResourceResolver.apply(R.string.event_food_location_set_addendum);
                description = String.format(template, newEntity.locationName);
            }
            partialSentences.add(description);
        } else if (oldEntity.location != 0 && newEntity.location == 0) {
            if (partialSentences.size() == 0) {
                template = stringResourceResolver.apply(R.string.event_food_location_unset);
                description = String.format(template, initiatorUser.name, oldEntity.locationName, oldEntity.name);
            } else {
                template = stringResourceResolver.apply(R.string.event_food_location_unset_addendum);
                description = String.format(template, oldEntity.locationName);
            }
            partialSentences.add(description);
        } else if (oldEntity.location != newEntity.location) {
            if (partialSentences.size() == 0) {
                template = stringResourceResolver.apply(R.string.event_food_location_changed);
                description = String.format(template, initiatorUser.name, oldEntity.name, oldEntity.locationName, newEntity.locationName);
            } else {
                template = stringResourceResolver.apply(R.string.event_food_location_changed_addendum);
                description = String.format(template, oldEntity.locationName, newEntity.locationName);
            }
            partialSentences.add(description);
        }

        if (oldEntity.expirationOffset != newEntity.expirationOffset) {
            if (partialSentences.size() == 0) {
                template = stringResourceResolver.apply(R.string.event_food_expiration_offset_set);
                description = String.format(template, initiatorUser.name, oldEntity.name, newEntity.expirationOffset);
            } else {
                template = stringResourceResolver.apply(R.string.event_food_expiration_offset_set_addendum);
                description = String.format(template, newEntity.expirationOffset);
            }
            partialSentences.add(description);
        }

        StringBuilder result = new StringBuilder();
        String enumerationSeparator = stringResourceResolver.apply(R.string.event_enumeration_item_divider);
        for (int i = 0; i < partialSentences.size() - 1; i++) {
            result.append(partialSentences.get(i));
            result.append(enumerationSeparator);
            result.append(" ");
        }

        if (partialSentences.size() > 1) {
            result.deleteCharAt(result.length() - 2);
            result.append(stringResourceResolver.apply(R.string.event_enumeration_item_divider_last));
            result.append(" ");
        }

        result.append(partialSentences.get(partialSentences.size() - 1));
        result.append(stringResourceResolver.apply(R.string.event_end_of_sentence));

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

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
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.entities.User;
import de.njsm.stocks.android.db.entities.UserDevice;

public class ChangedLocationEvent extends ChangedEntityEvent<Location> implements LocationIconResourceProvider {

    public ChangedLocationEvent(User initiatorUser, UserDevice initiatorDevice, Location oldEntity, Location newEntity) {
        super(initiatorUser, initiatorDevice, oldEntity, newEntity);
    }

    @Override
    public String describe(IntFunction<String> stringResourceResolver) {
        String template;
        String description;
        ArrayList<String> partialSentences = new ArrayList<>();

        if (!oldEntity.name.equals(newEntity.name)) {
            template = stringResourceResolver.apply(R.string.event_location_renamed);
            description = String.format(template, initiatorUser.name, oldEntity.name, newEntity.name);
            partialSentences.add(description);
        }

        if (!oldEntity.description.equals(newEntity.description)) {
            if (partialSentences.size() == 0) {
                template = stringResourceResolver.apply(R.string.event_location_description_changed);
                description = String.format(template, initiatorUser.name, oldEntity.name);
            } else {
                description = stringResourceResolver.apply(R.string.event_location_description_changed_addendum);
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
    public <I, O> O accept(EventVisitor<I, O> visitor, I arg) {
        return visitor.changedLocationEvent(this, arg);
    }
}

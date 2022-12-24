/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.client.databind.event;

import de.njsm.stocks.client.business.entities.UnitAmount;
import de.njsm.stocks.client.business.entities.event.*;
import de.njsm.stocks.client.presenter.DateRenderStrategy;
import de.njsm.stocks.client.presenter.UnitAmountRenderStrategy;
import de.njsm.stocks.client.ui.R;

import java.util.function.Function;

public class EventDescriptionRenderer implements Visitor<Void, String> {

    private final Function<Integer, String> dictionary;

    private final DateRenderStrategy dateRenderStrategy;

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    public EventDescriptionRenderer(Function<Integer, String> dictionary, DateRenderStrategy dateRenderStrategy) {
        this.dictionary = dictionary;
        this.dateRenderStrategy = dateRenderStrategy;
        unitAmountRenderStrategy = new UnitAmountRenderStrategy();
    }

    String visit(ActivityEvent event) {
        return visit(event, null);
    }

    @Override
    public String userCreated(UserCreatedEvent userCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_user_created);
        return String.format(template, userCreatedEvent.userName(), userCreatedEvent.name());
    }

    @Override
    public String userDeleted(UserDeletedEvent userDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_user_deleted);
        return String.format(template, userDeletedEvent.userName(), userDeletedEvent.name());
    }

    @Override
    public String userDeviceCreated(UserDeviceCreatedEvent userDeviceCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_user_device_created);
        return String.format(template,
                userDeviceCreatedEvent.userName(),
                userDeviceCreatedEvent.name(),
                userDeviceCreatedEvent.ownerName());
    }

    @Override
    public String userDeviceDeleted(UserDeviceDeletedEvent userDeviceDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_user_device_deleted);
        return String.format(template,
                userDeviceDeletedEvent.userName(),
                userDeviceDeletedEvent.ownerName(),
                userDeviceDeletedEvent.name());
    }

    @Override
    public String locationCreated(LocationCreatedEvent locationCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_location_created);
        return String.format(template,
                locationCreatedEvent.userName(),
                locationCreatedEvent.name());
    }

    @Override
    public String locationDeleted(LocationDeletedEvent locationDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_location_deleted);
        return String.format(template,
                locationDeletedEvent.userName(),
                locationDeletedEvent.name());
    }

    @Override
    public String locationEdited(LocationEditedEvent locationEditedEvent, Void input) {
        return locationEditedEvent.toString();
    }

    @Override
    public String foodCreated(FoodCreatedEvent foodCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_food_created);
        String mainSentence = String.format(template,
                foodCreatedEvent.userName(),
                foodCreatedEvent.name());

        if (foodCreatedEvent.toBuy()) {
            String shoppingListSubTemplate = dictionary.apply(R.string.event_food_to_buy_set);
            return mainSentence + " "
                    + dictionary.apply(R.string.event_enumeration_item_divider_last)
                    + " "
                    + String.format(shoppingListSubTemplate,
                            dictionary.apply(R.string.event_enumeration_undefined_object))
                    + ".";

        } else {
            return mainSentence + ".";
        }
    }

    @Override
    public String foodDeletedEvent(FoodDeletedEvent foodDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_food_deleted);
        return String.format(template,
                foodDeletedEvent.userName(),
                foodDeletedEvent.name());
    }

    @Override
    public String foodEditedEvent(FoodEditedEvent foodEditedEvent, Void input) {
        return foodEditedEvent.toString();
    }

    @Override
    public String foodItemCreated(FoodItemCreatedEvent foodItemCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_food_item_added);
        return String.format(template,
                foodItemCreatedEvent.userName(),
                unitAmountRenderStrategy.render(foodItemCreatedEvent.unit()),
                foodItemCreatedEvent.locationName(),
                dateRenderStrategy.render(foodItemCreatedEvent.eatBy().toLocalDate()));
    }

    @Override
    public String foodItemDeleted(FoodItemDeletedEvent foodItemDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_food_item_added);
        return String.format(template, foodItemDeletedEvent.userName(), foodItemDeletedEvent.foodName());
    }

    @Override
    public String foodItemEdited(FoodItemEditedEvent foodItemEditedEvent, Void input) {
        return foodItemEditedEvent.toString();
    }

    @Override
    public String scaledUnitCreated(ScaledUnitCreatedEvent scaledUnitCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_scaled_unit_created);
        return String.format(template,
                scaledUnitCreatedEvent.userName(),
                unitAmountRenderStrategy.render(
                        UnitAmount.of(scaledUnitCreatedEvent.scale(), scaledUnitCreatedEvent.abbreviation())
                ));
    }

    @Override
    public String scaledUnitDeleted(ScaledUnitDeletedEvent scaledUnitDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_scaled_unit_deleted);
        return String.format(template,
                scaledUnitDeletedEvent.userName(),
                unitAmountRenderStrategy.render(
                        UnitAmount.of(scaledUnitDeletedEvent.scale(), scaledUnitDeletedEvent.abbreviation())
                ));
    }

    @Override
    public String scaledUnitEdited(ScaledUnitEditedEvent scaledUnitEditedEvent, Void input) {
        return scaledUnitEditedEvent.toString();
    }

    @Override
    public String unitCreated(UnitCreatedEvent unitCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_unit_created);
        return String.format(template,
                unitCreatedEvent.userName(),
                unitCreatedEvent.name(),
                unitCreatedEvent.abbreviation());
    }

    @Override
    public String unitDeleted(UnitDeletedEvent unitDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_unit_deleted);
        return String.format(template,
                unitDeletedEvent.userName(),
                unitDeletedEvent.name(),
                unitDeletedEvent.abbreviation());
    }

    @Override
    public String unitEdited(UnitEditedEvent unitEditedEvent, Void input) {
        return unitEditedEvent.toString();
    }

    @Override
    public String eanNumberCreated(EanNumberCreatedEvent eanNumberCreatedEvent, Void input) {
        String template = dictionary.apply(R.string.event_eannumber_created);
        return String.format(template,
                eanNumberCreatedEvent.userName(),
                eanNumberCreatedEvent.eanNumber(),
                eanNumberCreatedEvent.foodName());
    }

    @Override
    public String eanNumberDeleted(EanNumberDeletedEvent eanNumberDeletedEvent, Void input) {
        String template = dictionary.apply(R.string.event_eannumber_deleted);
        return String.format(template,
                eanNumberDeletedEvent.userName(),
                eanNumberDeletedEvent.eanNumber(),
                eanNumberDeletedEvent.foodName());
    }
}

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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DescriptionRenderer implements Visitor<Void, String> {

    private final Function<Integer, String> dictionary;

    private final DateRenderStrategy dateRenderStrategy;

    private final UnitAmountRenderStrategy unitAmountRenderStrategy;

    public DescriptionRenderer(Function<Integer, String> dictionary, DateRenderStrategy dateRenderStrategy) {
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
    public String locationEdited(LocationEditedEvent event, Void input) {
        var object = formObject(event.name().former());
        return describe(event, List.of(
                LocationNameDiffer.of(event, dictionary, object),
                LocationDescriptionDiffer.of(event, dictionary, object)
        ), object);
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
                    + dictionary.apply(R.string.event_end_of_sentence);

        } else {
            return mainSentence + dictionary.apply(R.string.event_end_of_sentence);
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
    public String foodEditedEvent(FoodEditedEvent event, Void input) {
        var object = formObject(event.name().former());
        return describe(event, List.of(
                FoodNameDiffer.of(event, dictionary, object),
                FoodToBuyDiffer.of(event, dictionary, object),
                FoodExpirationOffsetDiffer.of(event, dictionary, object),
                FoodUnitDiffer.of(event, dictionary, object, unitAmountRenderStrategy),
                FoodLocationDiffer.of(event, dictionary, object),
                FoodDescriptionDiffer.of(event, dictionary, object)
        ), object);
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
    public String foodItemEdited(FoodItemEditedEvent event, Void input) {
        var object = formObject(event.foodName());
        return describe(event, List.of(
                FoodItemEatByDiffer.of(event, dictionary, object, dateRenderStrategy),
                FoodItemLocationDiffer.of(event, dictionary, object),
                FoodItemUnitDiffer.of(event, dictionary, object, unitAmountRenderStrategy),
                FoodItemBuyerDiffer.of(event, dictionary, object),
                FoodItemRegistererDiffer.of(event, dictionary, object)
        ), object);
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
    public String scaledUnitEdited(ScaledUnitEditedEvent event, Void input) {
        var object = formObject(unitAmountRenderStrategy.render(UnitAmount.of(event.scale().former(), event.abbreviation().former())));
        return describe(event, List.of(
                ScaledUnitScaleDiffer.of(event, dictionary, object, unitAmountRenderStrategy),
                ScaledUnitUnitDiffer.of(event, dictionary, object)
        ), object);
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
    public String unitEdited(UnitEditedEvent event, Void input) {
        var object = formObject(event.name().former());
        return describe(event, List.of(
                UnitNameDiffer.of(event, dictionary, object),
                UnitAbbreviationDiffer.of(event, dictionary, object)
        ), object);
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

    public <T extends ActivityEvent> String describe(T event, List<PartialDiffGenerator<?>> differs, SentenceObject object) {
        ArrayList<String> partialSentences = generatePartialSentences(differs);

        StringBuilder result = new StringBuilder();
        result.append(event.userName());
        result.append(" ");

        if (partialSentences.isEmpty()) {
            return unknownChange(object, result);
        } else {
            return assemblePartialSentences(partialSentences, result);
        }
    }

    @NotNull
    private ArrayList<String> generatePartialSentences(List<PartialDiffGenerator<?>> differs) {
        ArrayList<String> partialSentences = new ArrayList<>();
        for (PartialDiffGenerator<?> differ : differs) {
            differ.generate(partialSentences::add);
        }
        return partialSentences;
    }

    @NotNull
    private String unknownChange(SentenceObject object, StringBuilder result) {
        String template = dictionary.apply(R.string.event_unknown_change);
        result.append(String.format(template, object.get()));
        return result.toString();
    }

    @NotNull
    private String assemblePartialSentences(ArrayList<String> partialSentences, StringBuilder result) {
        String enumerationSeparator = dictionary.apply(R.string.event_enumeration_item_divider);
        for (int i = 0; i < partialSentences.size() - 1; i++) {
            result.append(partialSentences.get(i));
            result.append(enumerationSeparator);
            result.append(" ");
        }

        if (partialSentences.size() > 1) {
            result.deleteCharAt(result.length() - 2);
            result.append(dictionary.apply(R.string.event_enumeration_item_divider_last));
            result.append(" ");
        }

        result.append(partialSentences.get(partialSentences.size() - 1));
        result.append(dictionary.apply(R.string.event_end_of_sentence));

        return result.toString();
    }

    private SentenceObject formObject(String explicitObject) {
        return new SentenceObject(explicitObject,
                dictionary.apply(R.string.event_enumeration_undefined_object),
                String.format(dictionary.apply(R.string.event_enumeration_undefined_object_genitive), explicitObject));
    }
}

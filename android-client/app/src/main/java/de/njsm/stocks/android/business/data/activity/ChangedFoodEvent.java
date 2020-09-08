package de.njsm.stocks.android.business.data.activity;

import java.util.function.IntFunction;

import de.njsm.stocks.R;
import de.njsm.stocks.android.db.views.FoodWithLocationName;

public class ChangedFoodEvent extends ChangedEntityEvent<FoodWithLocationName> implements FoodIconResourceProvider {

    private int eventIcon;

    public ChangedFoodEvent(FoodWithLocationName former, FoodWithLocationName current) {
        super(former, current);

        if (former.toBuy != current.toBuy)
            eventIcon = R.drawable.ic_shopping_cart_black_24;
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
}

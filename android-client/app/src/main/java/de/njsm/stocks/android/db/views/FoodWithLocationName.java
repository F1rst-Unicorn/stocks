package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.db.entities.Food;

public class FoodWithLocationName extends Food {

    public String locationName;

    public FoodWithLocationName(int position, int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, String name, boolean toBuy, int expirationOffset, int location, String locationName) {
        super(position, id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, name, toBuy, expirationOffset, location);
        this.locationName = locationName;
    }
}

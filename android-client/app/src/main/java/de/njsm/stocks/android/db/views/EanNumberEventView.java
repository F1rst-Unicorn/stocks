package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;

import org.threeten.bp.Instant;

import de.njsm.stocks.android.db.entities.EanNumber;

public class EanNumberEventView extends EanNumber {

    public String identifiedFoodName;

    public EanNumberEventView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, String eanCode, int identifiesFood, String identifiedFoodName) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, eanCode, identifiesFood);
        this.identifiedFoodName = identifiedFoodName;
    }
}

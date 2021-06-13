/*
 * stocks is client-server program to manage a household's food stock
 * Copyright (C) 2021  The stocks developers
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

package de.njsm.stocks.android.business.data.conflict;

import android.os.Parcel;
import android.os.Parcelable;
import de.njsm.stocks.android.db.entities.FoodItem;
import de.njsm.stocks.android.util.Config;
import org.threeten.bp.Instant;

public class FoodItemInConflict implements Parcelable {

    private final int id;

    private final int version;

    private final Instant transactionTimeStart;

    private final Instant eatByDate;

    private final int location;

    private final int unit;

    protected FoodItemInConflict(Parcel in) {
        id = in.readInt();
        version = in.readInt();
        transactionTimeStart = Config.API_DATE_FORMAT.parse(in.readString(), Instant::from);
        eatByDate = Config.API_DATE_FORMAT.parse(in.readString(), Instant::from);
        location = in.readInt();
        unit = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(version);
        dest.writeString(Config.API_DATE_FORMAT.format(transactionTimeStart));
        dest.writeString(Config.API_DATE_FORMAT.format(eatByDate));
        dest.writeInt(location);
        dest.writeInt(unit);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FoodItemInConflict> CREATOR = new Creator<FoodItemInConflict>() {
        @Override
        public FoodItemInConflict createFromParcel(Parcel in) {
            return new FoodItemInConflict(in);
        }

        @Override
        public FoodItemInConflict[] newArray(int size) {
            return new FoodItemInConflict[size];
        }
    };

    public static FoodItemInConflict from(FoodItem foodItem) {
        return new FoodItemInConflict(
                foodItem.getId(),
                foodItem.getVersion(),
                foodItem.getTransactionTimeStart(),
                foodItem.getEatByDate(),
                foodItem.getStoredIn(),
                foodItem.getUnit()
        );
    }

    public FoodItemInConflict(int id, int version, Instant transactionTimeStart, Instant eatByDate, int location, int unit) {
        this.id = id;
        this.version = version;
        this.transactionTimeStart = transactionTimeStart;
        this.eatByDate = eatByDate;
        this.location = location;
        this.unit = unit;
    }

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public Instant getTransactionTimeStart() {
        return transactionTimeStart;
    }

    public Instant getEatByDate() {
        return eatByDate;
    }

    public int getLocation() {
        return location;
    }

    public int getUnit() {
        return unit;
    }
}

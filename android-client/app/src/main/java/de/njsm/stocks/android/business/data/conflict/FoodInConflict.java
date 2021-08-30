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
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.util.Config;
import java.time.Instant;

public class FoodInConflict implements Parcelable {

    private final int id;

    private final int version;

    private final Instant transactionTimeStart;

    private final String name;

    private final int expirationOffset;

    private final int location;

    private final int storeUnit;

    private final String description;

    public static FoodInConflict from(Food food) {
        return new FoodInConflict(
                food.id,
                food.version,
                food.transactionTimeStart,
                food.name,
                food.expirationOffset,
                food.location,
                food.storeUnit,
                food.description
        );
    }

    private FoodInConflict(int id, int version, Instant transactionTimeStart, String name, int expirationOffset, int location, int storeUnit, String description) {
        this.id = id;
        this.version = version;
        this.transactionTimeStart = transactionTimeStart;
        this.name = name;
        this.expirationOffset = expirationOffset;
        this.location = location;
        this.storeUnit = storeUnit;
        this.description = description;
    }

    protected FoodInConflict(Parcel in) {
        id = in.readInt();
        version = in.readInt();
        name = in.readString();
        transactionTimeStart = Config.API_DATE_FORMAT.parse(in.readString(), Instant::from);
        expirationOffset = in.readInt();
        location = in.readInt();
        storeUnit = in.readInt();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(version);
        dest.writeString(name);
        dest.writeString(Config.API_DATE_FORMAT.format(transactionTimeStart));
        dest.writeInt(expirationOffset);
        dest.writeInt(location);
        dest.writeInt(storeUnit);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FoodInConflict> CREATOR = new Creator<FoodInConflict>() {
        @Override
        public FoodInConflict createFromParcel(Parcel in) {
            return new FoodInConflict(in);
        }

        @Override
        public FoodInConflict[] newArray(int size) {
            return new FoodInConflict[size];
        }
    };

    public int getId() {
        return id;
    }

    public int getVersion() {
        return version;
    }

    public Instant getTransactionTimeStart() {
        return transactionTimeStart;
    }

    public String getName() {
        return name;
    }

    public int getExpirationOffset() {
        return expirationOffset;
    }

    public int getLocation() {
        return location;
    }

    public int getStoreUnit() {
        return storeUnit;
    }

    public String getDescription() {
        return description;
    }
}

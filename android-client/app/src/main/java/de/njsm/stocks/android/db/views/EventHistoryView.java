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

package de.njsm.stocks.android.db.views;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;

import de.njsm.stocks.android.business.data.activity.ChangedLocationEvent;
import de.njsm.stocks.android.business.data.activity.DeletedEanNumberEvent;
import de.njsm.stocks.android.business.data.activity.DeletedFoodEvent;
import de.njsm.stocks.android.business.data.activity.DeletedLocationEvent;
import de.njsm.stocks.android.business.data.activity.EntityEvent;
import de.njsm.stocks.android.business.data.activity.NewEanNumberEvent;
import de.njsm.stocks.android.business.data.activity.NewFoodEvent;
import de.njsm.stocks.android.business.data.activity.NewLocationEvent;
import de.njsm.stocks.android.db.entities.EanNumber;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.Location;
import de.njsm.stocks.android.db.entities.VersionedData;
import de.njsm.stocks.android.util.Config;

public class EventHistoryView {

    @ColumnInfo(name = "entity")
    private final String entityName;

    @Embedded(prefix = "version1_")
    private VersionedData version1;

    @Embedded(prefix = "version2_")
    private VersionedData version2;

    @ColumnInfo(name = "location1_name")
    private String locationName1;

    @ColumnInfo(name = "location2_name")
    private String locationName2;

    @ColumnInfo(name = "eannumber1_number")
    private String eannumberNumber1;

    @ColumnInfo(name = "eannumber2_number")
    private String eannumberNumber2;

    @ColumnInfo(name = "food1_name")
    private String foodName1;

    @ColumnInfo(name = "food2_name")
    private String foodName2;

    @ColumnInfo(name = "food1_to_buy")
    private int foodToBuy1;

    @ColumnInfo(name = "food2_to_buy")
    private int foodToBuy2;

    @ColumnInfo(name = "food1_expiration_offset")
    private int foodExpirationOffset1;

    @ColumnInfo(name = "food2_expiration_offset")
    private int getFoodExpirationOffset2;

    @ColumnInfo(name = "food1_location")
    private int foodLocation1;

    @ColumnInfo(name = "food2_location")
    private int foodLocation2;

    public EventHistoryView(String entityName, VersionedData version1, VersionedData version2, String locationName1, String locationName2, String eannumberNumber1, String eannumberNumber2, String foodName1, String foodName2, int foodToBuy1, int foodToBuy2, int foodExpirationOffset1, int getFoodExpirationOffset2, int foodLocation1, int foodLocation2) {
        this.entityName = entityName;
        this.version1 = version1;
        this.version2 = version2;
        this.locationName1 = locationName1;
        this.locationName2 = locationName2;
        this.eannumberNumber1 = eannumberNumber1;
        this.eannumberNumber2 = eannumberNumber2;
        this.foodName1 = foodName1;
        this.foodName2 = foodName2;
        this.foodToBuy1 = foodToBuy1;
        this.foodToBuy2 = foodToBuy2;
        this.foodExpirationOffset1 = foodExpirationOffset1;
        this.getFoodExpirationOffset2 = getFoodExpirationOffset2;
        this.foodLocation1 = foodLocation1;
        this.foodLocation2 = foodLocation2;
    }

    public EntityEvent<?> mapToEvent() {
        if (version1.version == 0 && version2 == null && version1.validTimeEnd.equals(Config.API_INFINITY)) {
            switch (entityName) {
                case "location":
                    return new NewLocationEvent(getLocation1());
                case "eannumber":
                    return new NewEanNumberEvent(getEanNumber1());
                case "food":
                    return new NewFoodEvent(getFood1());
            }
        } else if (version2 != null) {
            if (entityName.equals("location"))
                return new ChangedLocationEvent(getLocation1(), getLocation2());
        } else {
            switch (entityName) {
                case "location":
                    return new DeletedLocationEvent(getLocation1());
                case "eannumber":
                    return new DeletedEanNumberEvent(getEanNumber1());
                case "food":
                    return new DeletedFoodEvent(getFood1());
            }
        }
        return null;
    }

    private Food getFood1() {
        return new Food(0, version1.id, version1.validTimeStart, version1.validTimeEnd, version1.transactionTimeStart, version1.transactionTimeEnd, version1.version, foodName1, foodToBuy1 == 1, foodExpirationOffset1, foodLocation1);
    }

    private EanNumber getEanNumber1() {
        return new EanNumber(version1.id, version1.validTimeStart, version1.validTimeEnd, version1.transactionTimeStart, version1.transactionTimeEnd, version1.version, eannumberNumber1, 0);
    }

    private Location getLocation1() {
        return new Location(version1.id, version1.validTimeStart, version1.validTimeEnd, version1.transactionTimeStart, version1.transactionTimeEnd, version1.version, locationName1);
    }

    private Location getLocation2() {
        return new Location(version2.id, version2.validTimeStart, version2.validTimeEnd, version2.transactionTimeStart, version2.transactionTimeEnd, version2.version, locationName2);
    }
}

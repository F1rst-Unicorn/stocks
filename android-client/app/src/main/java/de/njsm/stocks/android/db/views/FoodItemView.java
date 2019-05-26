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

import de.njsm.stocks.android.db.entities.VersionedData;
import org.threeten.bp.Instant;

public class FoodItemView extends VersionedData {

    private String userName;

    private String deviceName;

    private Instant eatByDate;

    private String location;

    private int ofType;

    private int storedIn;

    public FoodItemView(int id,
                        int version,
                        int ofType, int storedIn, String userName,
                        String deviceName,
                        Instant eatByDate,
                        String location) {
        super(id, version);
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.userName = userName;
        this.deviceName = deviceName;
        this.eatByDate = eatByDate;
        this.location = location;
    }

    public String getUserName() {
        return userName;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public Instant getEatByDate() {
        return eatByDate;
    }

    public String getLocation() {
        return location;
    }

    public int getOfType() {
        return ofType;
    }

    public int getStoredIn() {
        return storedIn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodItemView that = (FoodItemView) o;

        if (!userName.equals(that.userName)) return false;
        if (!deviceName.equals(that.deviceName)) return false;
        if (!eatByDate.equals(that.eatByDate)) return false;
        return location.equals(that.location);
    }

    @Override
    public int hashCode() {
        int result = userName.hashCode();
        result = 31 * result + deviceName.hashCode();
        result = 31 * result + eatByDate.hashCode();
        result = 31 * result + location.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FoodItemView{" +
                "userName='" + userName + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", eatByDate=" + eatByDate +
                ", location='" + location + '\'' +
                ", version=" + version +
                ", id=" + id +
                '}';
    }
}

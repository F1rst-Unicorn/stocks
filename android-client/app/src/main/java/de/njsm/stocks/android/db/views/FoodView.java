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

import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.VersionedData;
import org.threeten.bp.Instant;

public class FoodView extends VersionedData {

    private int amount;

    private Instant eatBy;

    private String name;

    private boolean toBuy;

    public FoodView(int id, int version, int amount, Instant eatBy, String name, boolean toBuy) {
        super(id, version);
        this.amount = amount;
        this.eatBy = eatBy;
        this.name = name;
        this.toBuy = toBuy;
    }

    public Food mapToFood() {
        return new Food(id, version, name, toBuy, getPosition());
    }

    public int getAmount() {
        return amount;
    }

    public Instant getEatBy() {
        return eatBy;
    }

    public String getName() {
        return name;
    }

    public boolean getToBuy() {
        return toBuy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodView foodView = (FoodView) o;

        if (amount != foodView.amount) return false;
        if (!eatBy.equals(foodView.eatBy)) return false;
        return name.equals(foodView.name);
    }

    @Override
    public int hashCode() {
        int result = amount;
        result = 31 * result + eatBy.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "FoodView{" +
                "amount=" + amount +
                ", eatBy=" + eatBy +
                ", name='" + name + '\'' +
                ", version=" + version +
                ", id=" + id +
                '}';
    }
}

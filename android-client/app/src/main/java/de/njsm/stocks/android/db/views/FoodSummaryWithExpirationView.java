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

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import org.threeten.bp.Instant;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FoodSummaryWithExpirationView extends FoodSummaryView {

    private final Instant eatBy;

    public FoodSummaryWithExpirationView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, @NonNull String description, int storeUnit, Instant eatBy) {
        super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit);
        this.eatBy = eatBy;
    }

    @Override
    public FoodSummaryWithExpirationView merge(FoodSummaryView.SingleFoodSummaryView other) {
        super.merge(other);
        return this;
    }

    public Instant getEatBy() {
        return eatBy;
    }

    public static class SingleFoodSummaryView extends FoodSummaryView.SingleFoodSummaryView {

        private final Instant eatBy;

        public SingleFoodSummaryView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, @NonNull String description, int storeUnit, ScaledAmount amount, Instant eatBy) {
            super(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit, amount);
            this.eatBy = eatBy;
        }

        public FoodSummaryWithExpirationView into() {
            FoodSummaryWithExpirationView result = new FoodSummaryWithExpirationView(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit, eatBy);
            result.getAmounts().add(getAmount());
            return result;
        }
    }

    public static class Mapper implements Function<List<SingleFoodSummaryView>, List<FoodSummaryWithExpirationView>> {
        @Override
        public List<FoodSummaryWithExpirationView> apply(List<SingleFoodSummaryView> foodSummaryView) {
            return StreamSupport.stream(new Spliterator<>(foodSummaryView.iterator(), SingleFoodSummaryView::into, (i, o) -> o.merge(i)), false)
                    .collect(Collectors.toList());
        }
    }
}

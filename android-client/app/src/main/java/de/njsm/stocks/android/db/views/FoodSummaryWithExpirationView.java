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

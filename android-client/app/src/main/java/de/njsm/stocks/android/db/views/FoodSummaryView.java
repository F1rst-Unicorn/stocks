package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.room.Embedded;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.util.Aggregator;
import org.threeten.bp.Instant;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FoodSummaryView extends Food {

    private final List<ScaledAmount> amounts;

    public FoodSummaryView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, @NonNull String description, int storeUnit) {
        super(0, id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit);
        this.amounts = new ArrayList<>();
    }

    public List<ScaledAmount> getAmounts() {
        return amounts;
    }

    public FoodSummaryView merge(SingleFoodSummaryView other) {
        amounts.add(other.amount);
        return this;
    }

    public String printAmounts() {
        return ScaledAmount.getPrettyString(getAmounts());
    }

    public static class SingleFoodSummaryView extends Food {

        @Embedded
        private final ScaledAmount amount;

        public SingleFoodSummaryView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, @NonNull String description, int storeUnit, ScaledAmount amount) {
            super(0, id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit);
            this.amount = amount;
        }

        public ScaledAmount getAmount() {
            return amount;
        }

        public FoodSummaryView into() {
            FoodSummaryView result = new FoodSummaryView(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit);
            result.getAmounts().add(amount);
            return result;
        }
    }

    public static class Mapper implements Function<List<SingleFoodSummaryView>, List<FoodSummaryView>> {
        @Override
        public List<FoodSummaryView> apply(List<SingleFoodSummaryView> foodSummaryView) {
            return StreamSupport.stream(new Spliterator<>(foodSummaryView.iterator(), SingleFoodSummaryView::into, (i, o) -> o.merge(i)), false)
                    .collect(Collectors.toList());
        }
    }

    public static class Spliterator<I extends SingleFoodSummaryView, O extends FoodSummaryView> extends Aggregator<I, O> {

        private final Function<I, O> base;

        private final BiFunction<I, O, O> folder;

        public Spliterator(Iterator<I> iterator, Function<I, O> base, BiFunction<I, O, O> folder) {
            super(iterator);
            this.base = base;
            this.folder = folder;
        }

        @Override
        public O base(I input) {
            return base.apply(input);
        }

        @Override
        public boolean sameGroup(O current, I input) {
            return current.getId() == input.getId();
        }

        @Override
        public O merge(O current, I input) {
            return folder.apply(input, current);
        }
    }
}

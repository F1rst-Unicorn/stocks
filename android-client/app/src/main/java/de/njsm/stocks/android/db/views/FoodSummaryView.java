package de.njsm.stocks.android.db.views;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.room.Embedded;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.entities.Sql;
import de.njsm.stocks.android.db.entities.Unit;
import org.threeten.bp.Instant;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FoodSummaryView extends Food {

    private final Instant eatBy;

    private final List<ScaledAmount> amounts;

    public FoodSummaryView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, @NonNull String description, int storeUnit, Instant eatBy) {
        super(0, id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit);
        this.eatBy = eatBy;
        this.amounts = new ArrayList<>();
    }

    public Instant getEatBy() {
        return eatBy;
    }

    public List<ScaledAmount> getAmounts() {
        return amounts;
    }

    public FoodSummaryView merge(SingleFoodSummaryView other) {
        amounts.add(other.amount);
        return this;
    }

    public static class SingleFoodSummaryView extends Food {

        private final Instant eatBy;

        @Embedded
        private final ScaledAmount amount;

        public SingleFoodSummaryView(int id, @NonNull Instant validTimeStart, @NonNull Instant validTimeEnd, @NonNull Instant transactionTimeStart, @NonNull Instant transactionTimeEnd, int version, int initiates, String name, boolean toBuy, int expirationOffset, int location, @NonNull String description, int storeUnit, Instant eatBy, ScaledAmount amount) {
            super(0, id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit);
            this.eatBy = eatBy;
            this.amount = amount;
        }

        public FoodSummaryView into() {
            FoodSummaryView result = new FoodSummaryView(id, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, version, initiates, name, toBuy, expirationOffset, location, description, storeUnit, eatBy);
            result.getAmounts().add(amount);
            return result;
        }
    }

    public static class ScaledAmount {

        private final int amount;

        @Embedded(prefix = Sql.SCALED_UNIT_PREFIX)
        private final ScaledUnit scaledUnit;

        @Embedded(prefix = Sql.UNIT_PREFIX)
        private final Unit unit;

        public ScaledAmount(int amount, ScaledUnit scaledUnit, Unit unit) {
            this.amount = amount;
            this.scaledUnit = scaledUnit;
            this.unit = unit;
        }

        public int getAmount() {
            return amount;
        }

        public ScaledUnit getScaledUnit() {
            return scaledUnit;
        }

        public Unit getUnit() {
            return unit;
        }

        public String getPrettyString() {
            ScaledUnit copy = getScaledUnit().copy();
            copy.setScale(copy.getScale().multiply(new BigDecimal(getAmount())));
            return ScaledUnitView.getPrettyName(copy, getUnit());
        }
    }

    public static class Mapper implements Function<List<SingleFoodSummaryView>, List<FoodSummaryView>> {
        @Override
        public List<FoodSummaryView> apply(List<SingleFoodSummaryView> foodSummaryView) {
            return StreamSupport.stream(new Spliterator(foodSummaryView.iterator()), false)
                    .collect(Collectors.toList());
        }
    }

    private static class Spliterator implements java.util.Spliterator<FoodSummaryView> {

        private final Iterator<SingleFoodSummaryView> iterator;

        private FoodSummaryView current;

        public Spliterator(Iterator<SingleFoodSummaryView> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean tryAdvance(Consumer<? super FoodSummaryView> action) {
            while (iterator.hasNext()) {
                SingleFoodSummaryView r = iterator.next();
                if (current == null) {
                    current = r.into();
                } else {
                    if (current.getId() == r.getId()) {
                        current = current.merge(r);
                    } else {
                        action.accept(current);
                        current = r.into();
                        return true;
                    }
                }
            }

            if (current != null) {
                action.accept(current);
                current = null;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public java.util.Spliterator<FoodSummaryView> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return iterator.hasNext() ? Long.MAX_VALUE : 0;
        }

        @Override
        public int characteristics() {
            return ORDERED | NONNULL | IMMUTABLE;        }
    }
}

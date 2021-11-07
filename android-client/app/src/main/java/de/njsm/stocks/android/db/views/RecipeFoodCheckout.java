package de.njsm.stocks.android.db.views;

import androidx.arch.core.util.Function;
import androidx.room.Embedded;
import com.google.common.collect.Lists;
import de.njsm.stocks.android.db.util.Aggregator;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RecipeFoodCheckout {

    private final ScaledFood scaledFood;

    private final boolean toBuy;

    private final List<ScaledAmount> currentStock;

    private final List<Integer> distribution;

    public RecipeFoodCheckout(ScaledFood scaledFood, boolean toBuy, List<ScaledAmount> currentStock) {
        this.scaledFood = scaledFood;
        this.toBuy = toBuy;
        this.currentStock = currentStock;
        this.distribution = new ArrayList<>(currentStock.size());
    }

    public ScaledFood getScaledFood() {
        return scaledFood;
    }

    public boolean isToBuy() {
        return toBuy;
    }

    public List<ScaledAmount> getCurrentStock() {
        return currentStock;
    }

    public List<Integer> getDistribution() {
        if (distribution.size() != currentStock.size())
            computeDistributions();
        return distribution;
    }

    private void computeDistributions() {
        distribution.clear();

        List<ScaledAmount> sameUnit = new ArrayList<>();

        for (ScaledAmount amount : currentStock) {
            if (amount.getUnit().getId() == scaledFood.getUnit().getId()) {
                sameUnit.add(amount);
            }
        }

        sameUnit.sort((o1, o2) -> - o1.getScaledUnit().getScale().compareTo(o2.getScaledUnit().getScale()));
        BigDecimal requiredAmount = scaledFood.getScaledUnit().getScale().multiply(BigDecimal.valueOf(scaledFood.getAmount()));

        Map<ScaledAmount, Integer> distributions = new HashMap<>();

        for (ScaledAmount amount : sameUnit) {
            int requiredItems = requiredAmount.divideToIntegralValue(amount.getScaledUnit().getScale()).intValue();
            int feasibleItems = Integer.min(requiredItems, amount.getAmount());
            distributions.put(amount, feasibleItems);
            requiredAmount = requiredAmount.subtract(amount.getScaledUnit().getScale().multiply(BigDecimal.valueOf(feasibleItems)));
        }

        for (ScaledAmount amount : currentStock) {
            if (amount.getUnit().getId() != scaledFood.getUnit().getId()) {
                distribution.add(0);
            } else {
                distribution.add(distributions.get(amount));
            }
        }
    }

    private RecipeFoodCheckout merge(SingleRecipeFoodCheckout input) {
        currentStock.add(input.currentStock);
        return this;
    }

    public static class SingleRecipeFoodCheckout {

        @Embedded
        private final ScaledFood scaledFood;

        private final boolean toBuy;

        @Embedded(prefix = de.njsm.stocks.android.db.dbview.ScaledAmount.SCALED_AMOUNT_PREFIX)
        private final ScaledAmount currentStock;

        public SingleRecipeFoodCheckout(ScaledFood scaledFood, boolean toBuy, ScaledAmount currentStock) {
            this.scaledFood = scaledFood;
            this.toBuy = toBuy;
            this.currentStock = currentStock;
        }
        public RecipeFoodCheckout into() {
            return new RecipeFoodCheckout(scaledFood, toBuy, Lists.newArrayList(currentStock));
        }

    }
    public static class Mapper implements Function<List<RecipeFoodCheckout.SingleRecipeFoodCheckout>, List<RecipeFoodCheckout>> {

        @Override
        public List<RecipeFoodCheckout> apply(List<RecipeFoodCheckout.SingleRecipeFoodCheckout> input) {
            return StreamSupport.stream(new RecipeFoodCheckout.Spliterator(input.iterator()), false)
                    .collect(Collectors.toList());
        }

    }
    public static class Spliterator extends Aggregator<RecipeFoodCheckout.SingleRecipeFoodCheckout, RecipeFoodCheckout> {


        public Spliterator(Iterator<RecipeFoodCheckout.SingleRecipeFoodCheckout> iterator) {
            super(iterator);
        }

        @Override
        public RecipeFoodCheckout base(RecipeFoodCheckout.SingleRecipeFoodCheckout input) {
            return input.into();
        }

        @Override
        public boolean sameGroup(RecipeFoodCheckout current, RecipeFoodCheckout.SingleRecipeFoodCheckout input) {
            return current.getScaledFood().getFood().getId() == input.scaledFood.getFood().getId();
        }
        @Override
        public RecipeFoodCheckout merge(RecipeFoodCheckout current, RecipeFoodCheckout.SingleRecipeFoodCheckout input) {
            return current.merge(input);
        }

    }

}

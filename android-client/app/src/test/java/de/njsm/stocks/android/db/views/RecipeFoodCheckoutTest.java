package de.njsm.stocks.android.db.views;

import com.google.common.collect.Lists;
import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.ScaledUnit;
import de.njsm.stocks.android.db.entities.Unit;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.fail;

public class RecipeFoodCheckoutTest {

    @Test
    public void oneMatchingFoodIsDistributed() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                5, 1, 1,
                5, 1, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(5)));
    }

    @Test
    public void oneMatchingFoodOfDifferentScaleIsDistributed() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                5, 1, 1,
                1, 5, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(1)));
    }

    @Test
    public void noMatchingFoodGivesEmptyList() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                5, 1, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList()));
    }

    @Test
    public void noMatchingFoodOfSameUnitGivesAZeroEntry() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                5, 1, 1,
                5, 1, 2);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(0)));
    }

    @Test
    public void compositeUnitsMatchExactly() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                5, 1, 1,
                2, 1, 1,
                3, 1, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(2, 3)));
    }

    @Test
    public void compositeUnitsOfDifferentScaleMatchExactly() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                5, 1, 1,
                2, 1, 1,
                1, 3, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(2, 1)));
    }

    @Test
    public void lessInStockThanRequiredIsOk() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                20, 1, 1,
                10, 1, 1,
                1, 3, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(10, 1)));
    }

    @Test
    public void interjectedDifferentUnitIsIgnored() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                20, 1, 1,
                10, 1, 1,
                10, 1, 2,
                1, 3, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(10, 0, 1)));
    }

    @Test
    public void oversufficingAmountIsOnlyTakenAsNeeded() {
        RecipeFoodCheckout uut = buildFoodToCheckout(
                100, 1, 1,
                2, 75, 1,
                3, 10, 1,
                3, 1, 1);

        List<Integer> distribution = uut.getDistribution();

        assertThat(distribution, is(Lists.newArrayList(1, 2, 3)));
    }

    private RecipeFoodCheckout buildFoodToCheckout(int requiredAmount, int requiredScale, int requiredUnit, int... vararg) {
        List<ScaledAmount> amounts = new ArrayList<>();

        if (vararg.length % 3 != 0)
            fail("Invalid input");

        for (int i = 0; i < vararg.length; i += 3) {
            amounts.add(new ScaledAmount(
                    vararg[i],
                    new ScaledUnit(
                            0, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, 0, 0, BigDecimal.valueOf(vararg[i+1]), vararg[i+2]
                    ),
                    new Unit(
                            vararg[i+2], Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, 0, 0, "", ""
                    )
            ));
        }

        return new RecipeFoodCheckout(
                new ScaledFood(
                        requiredAmount,
                        new Food(
                                0, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, 0, 0, "", false, 0, 0, "", 0
                        ),
                        new ScaledUnit(
                                0, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, 0, 0, BigDecimal.valueOf(requiredScale), requiredUnit
                        ),
                        new Unit(
                                requiredUnit, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, Instant.EPOCH, 0, 0, "", ""
                        )
                ),
                false,
                amounts
        );
    }
}

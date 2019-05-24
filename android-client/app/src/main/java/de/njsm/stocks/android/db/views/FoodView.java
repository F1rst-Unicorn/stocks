package de.njsm.stocks.android.db.views;

import de.njsm.stocks.android.db.entities.Food;
import de.njsm.stocks.android.db.entities.VersionedData;
import org.threeten.bp.Instant;

public class FoodView extends VersionedData {

    private int amount;

    private Instant eatBy;

    private String name;

    public FoodView(int id, int version, int amount, Instant eatBy, String name) {
        super(id, version);
        this.amount = amount;
        this.eatBy = eatBy;
        this.name = name;
    }

    public Food mapToFood() {
        return new Food(id, version, name);
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

package de.njsm.stocks.client.business.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.client.business.data.visitor.AbstractVisitor;
import de.njsm.stocks.client.business.json.InstantDeserialiser;
import de.njsm.stocks.client.business.json.InstantSerialiser;

import java.time.Instant;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties(ignoreUnknown = true)
public class FoodItem extends VersionedData {

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant eatByDate;

    public int ofType;

    public int storedIn;

    public int registers;

    public int buys;

    public FoodItem() {
    }

    public FoodItem(int id,
                    int version,
                    Instant eatByDate,
                    int ofType,
                    int storedIn,
                    int registers,
                    int buys) {
        super(id, version);
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    public FoodItem(Instant eatByDate,
                    int ofType,
                    int storedIn,
                    int registers,
                    int buys) {
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    public FoodItem(int id, int version) {
        super(id, version);
        eatByDate = Instant.EPOCH;
    }

    @Override
    public <I, O> O accept(AbstractVisitor<I, O> visitor, I input) {
        return visitor.foodItem(this, input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodItem foodItem = (FoodItem) o;

        if (id != foodItem.id) return false;
        if (ofType != foodItem.ofType) return false;
        if (storedIn != foodItem.storedIn) return false;
        if (registers != foodItem.registers) return false;
        if (buys != foodItem.buys) return false;
        return eatByDate.equals(foodItem.eatByDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eatByDate, ofType, storedIn, registers, buys);
    }

    @Override
    public String toString() {
        return "FoodItem (" + id + ", " + version + ", " + eatByDate + ", " + ofType + ", " + storedIn + ", " + registers + ", " + buys + ")";
    }
}

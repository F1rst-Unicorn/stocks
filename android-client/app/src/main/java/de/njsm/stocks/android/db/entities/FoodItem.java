package de.njsm.stocks.android.db.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.android.network.server.util.InstantDeserialiser;
import de.njsm.stocks.android.network.server.util.InstantSerialiser;
import org.threeten.bp.Instant;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@Entity
public class FoodItem extends VersionedData {

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    @ColumnInfo(name = "eat_by")
    public Instant eatByDate;

    @ColumnInfo(name = "of_type")
    public int ofType;

    @ColumnInfo(name = "stored_in")
    public int storedIn;

    @ColumnInfo(name = "registers")
    public int registers;

    @ColumnInfo(name = "buys")
    public int buys;

    @Ignore
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

    @Ignore
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

    @Ignore
    public FoodItem(int id, int version) {
        super(id, version);
        eatByDate = Instant.EPOCH;
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

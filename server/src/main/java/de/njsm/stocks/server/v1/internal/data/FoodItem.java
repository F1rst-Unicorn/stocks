package de.njsm.stocks.server.v1.internal.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;
import de.njsm.stocks.server.v2.business.json.InstantDeserialiser;
import de.njsm.stocks.server.v2.business.json.InstantSerialiser;
import java.time.Instant;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class FoodItem extends Data implements SqlRemovable {

    @JsonSerialize(using = InstantSerialiser.class)
    @JsonDeserialize(using = InstantDeserialiser.class)
    public Instant eatByDate;

    public int ofType;

    public int storedIn;

    public int registers;

    public int buys;

    public FoodItem(int id, Instant eatByDate, int ofType, int storedIn, int registers, int buys) {
        this.id = id;
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }

    public FoodItem() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.foodItem(this, input);
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM \"Food_item\" WHERE \"ID\"=?";
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
        int result = id;
        result = 31 * result + eatByDate.hashCode();
        result = 31 * result + ofType;
        result = 31 * result + storedIn;
        result = 31 * result + registers;
        result = 31 * result + buys;
        return result;
    }

    @Override
    public String toString() {
        return "FoodItem (" + id + ", " + eatByDate + ", " + ofType + ", " + storedIn + ", " + registers + ", " + buys + ")";
    }
}

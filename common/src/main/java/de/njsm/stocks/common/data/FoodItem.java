package de.njsm.stocks.common.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class FoodItem extends Data implements SqlAddable, SqlRemovable {

    public int id;

    public Date eatByDate;

    public int ofType;

    public int storedIn;

    public int registers;

    public int buys;

    public FoodItem(int id, Date eatByDate, int ofType, int storedIn, int registers, int buys) {
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
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setDate(1, new java.sql.Date(eatByDate.getTime()));
        stmt.setInt(2, ofType);
        stmt.setInt(3, storedIn);
        stmt.setInt(4, registers);
        stmt.setInt(5, buys);
    }

    @Override
    public void fillAddStmtWithId(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
        stmt.setDate(2, new java.sql.Date(eatByDate.getTime()));
        stmt.setInt(3, ofType);
        stmt.setInt(4, storedIn);
        stmt.setInt(5, registers);
        stmt.setInt(6, buys);
    }

    @Override
    public String getAddStmt() {
        return "INSERT INTO Food_item (eat_by, of_type, stored_in, registers, buys) " +
                "VALUES (?,?,?,?,?)";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM Food_item WHERE ID=?";
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
        return "FoodItem (" + id + ", " + eatByDate + ", " + ofType + ")";
    }
}

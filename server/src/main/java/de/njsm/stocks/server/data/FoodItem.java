package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Override
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setDate(1, new java.sql.Date(eatByDate.getTime()));
        stmt.setInt(2, ofType);
        stmt.setInt(3, storedIn);
        stmt.setInt(4, registers);
        stmt.setInt(5, buys);
    }

    @Override
    @JsonIgnore
    public String getAddStmt() {
        return "INSERT INTO Food_item (eat_by, of_type, stored_in, registers, buys) " +
                "VALUES (?,?,?,?,?)";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    @JsonIgnore
    public String getRemoveStmt() {
        return "DELETE FROM Food_item WHERE ID=?";
    }

    @Override
    public String toString() {
        return "FoodItem (" + id + ", " + eatByDate + ", " + ofType + ")";
    }
}

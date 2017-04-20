package de.njsm.stocks.common.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;
import de.njsm.stocks.common.data.visitor.VisitorException;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Food extends Data implements SqlRenamable, SqlRemovable {
    public int id;
    public String name;

    public Food(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Food() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) throws VisitorException {
        return visitor.food(this, input);
    }

    public void fillAddStmtWithId(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
        stmt.setString(2, name);
    }

    @Override
    public void fillRenameStmt(PreparedStatement stmt, String newName) throws SQLException {
        stmt.setString(1, newName);
        stmt.setInt(2, id);
    }

    @Override
    public String getRenameStmt() {
        return "UPDATE Food SET name=? WHERE ID=?";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM Food WHERE ID=?";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Food food = (Food) o;

        if (id != food.id) return false;
        return name.equals(food.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Food (" + id + ", " + name + ")";
    }
}

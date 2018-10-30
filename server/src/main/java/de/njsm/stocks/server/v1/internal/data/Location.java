package de.njsm.stocks.server.v1.internal.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class Location extends Data implements SqlRenamable, SqlRemovable {

    public String name;

    public Location(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Location() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.location(this, input);
    }

    @Override
    public void fillRenameStmt(PreparedStatement stmt, String newName) throws SQLException {
        stmt.setString(1, newName);
        stmt.setInt(2, id);
    }

    @Override
    public String getRenameStmt() {
        return "UPDATE \"Location\" SET \"name\"=? WHERE \"ID\"=?";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM \"Location\" WHERE \"ID\"=?";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (id != location.id) return false;
        return name.equals(location.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Location (" + id + ", " + name + ")";
    }
}

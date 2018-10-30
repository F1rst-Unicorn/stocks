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
public class User extends Data implements SqlRemovable {

    public String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public User() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.user(this, input);
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM \"User\" WHERE \"ID\"=?";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != user.id) return false;
        return name.equals(user.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User (" + id + ", " + name + ")";
    }
}

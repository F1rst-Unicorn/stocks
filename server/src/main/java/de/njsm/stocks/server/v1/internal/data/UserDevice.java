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
public class UserDevice extends Data implements SqlRemovable {

    public String name;

    public int userId;

    public UserDevice(int id, String name, int userId) {
        this.id = id;
        this.name = name;
        this.userId = userId;
    }

    public UserDevice() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.userDevice(this, input);
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM \"User_device\" WHERE \"ID\"=?";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDevice that = (UserDevice) o;

        if (id != that.id) return false;
        if (userId != that.userId) return false;
        return name.equals(that.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + userId;
        return result;
    }

    @Override
    public String toString() {
        return "Device (" + id + ", " + name + ")";
    }
}

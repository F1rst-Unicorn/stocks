package de.njsm.stocks.client.data;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class User extends Data implements SqlAddable, SqlRemovable {
    public int id;
    public String name;

    @Override
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, name);
    }

    @Override
    public String getAddStmt() {
        return "INSERT INTO User (name) VALUES (?)";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM User WHERE ID=?";
    }

    @Override
    public String toString() {
        return "User (" + id + ", " + name + ")";
    }
}

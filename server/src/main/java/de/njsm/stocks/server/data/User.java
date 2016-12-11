package de.njsm.stocks.server.data;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class User extends Data implements SqlAddable,
                                          SqlRemovable {
    public int id;
    public String name;

    public User() {
    }

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, name);
    }

    @Override
    @JsonIgnore
    public String getAddStmt() {
        return "INSERT INTO User (name) VALUES (?)";
    }

    @Override
    @JsonIgnore
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    @JsonIgnore
    public String getRemoveStmt() {
        return "DELETE FROM User WHERE ID=?";
    }

}

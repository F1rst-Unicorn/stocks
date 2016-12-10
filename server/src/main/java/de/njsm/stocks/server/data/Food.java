package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Food extends Data implements SqlAddable,
                                          SqlRenamable,
                                          SqlRemovable {

    public int id;
    public String name;

    public Food() {
    }

    public Food(int id, String name) {
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
        return "INSERT INTO Food (name) VALUES (?)";
    }

    @Override
    public void fillRenameStmt(PreparedStatement stmt, String newName) throws SQLException {
        stmt.setString(1, newName);
        stmt.setInt(2, id);
    }

    @Override
    @JsonIgnore
    public String getRenameStmt() {
        return "UPDATE Food SET name=? WHERE ID=?";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    @JsonIgnore
    public String getRemoveStmt() {
        return "DELETE FROM Food WHERE ID=?";
    }

    @Override
    public String toString() {
        return "Food (" + id + ", " + name + ")";
    }
}

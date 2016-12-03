package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Location extends Data implements SqlAddable,
                                              SqlRenamable,
                                              SqlRemovable {
    
    public int id;
    public String name;

    @Override
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, name);
    }

    @Override
    @JsonIgnore
    public String getAddStmt() {
        return "INSERT INTO Location (name) VALUES (?)";
    }

    @Override
    public void fillRenameStmt(PreparedStatement stmt, String newName) throws SQLException {
        stmt.setString(1, newName);
        stmt.setInt(2, id);
    }

    @Override
    @JsonIgnore
    public String getRenameStmt() {
        return "UPDATE Location SET name=? WHERE ID=?";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    @JsonIgnore
    public String getRemoveStmt() {
        return "DELETE FROM Location WHERE ID=?";
    }

    @Override
    public String toString() {
        return "Location (" + id + ", " + name + ")";
    }
}

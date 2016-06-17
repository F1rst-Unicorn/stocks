package de.njsm.stocks.server.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Food extends Data {
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
    public String toString() {
        return "Food (" + id + ", " + name + ")";
    }
}

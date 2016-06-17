package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class UserDevice implements SqlAddable{
    public int id;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    public Date lastUpdate;
    public String name;
    public int userId;

    @Override
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, name);
        stmt.setInt(2, userId);
    }

    @Override
    public String getAddStmt() {
        return "INSERT INTO User_device (name, belongs_to) VALUES (?,?)";
    }

    @Override
    public String toString() {
        return "Device (" + id + ", " + name + ")";
    }
}

package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class EanNumber extends Data implements SqlAddable, SqlRemovable {

    public int id;
    public String eanCode;
    public int identifiesFood;

    public EanNumber() {
    }

    public EanNumber(int id, String eanCode, int identifiesFood) {
        this.id = id;
        this.eanCode = eanCode;
        this.identifiesFood = identifiesFood;
    }

    @Override
    public void fillAddStmt(PreparedStatement stmt) throws SQLException {
        stmt.setString(1, eanCode);
        stmt.setInt(2, identifiesFood);
    }

    @Override
    @JsonIgnore
    public String getAddStmt() {
        return "INSERT INTO EAN_number (number, identifies) VALUES (?,?)";
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, id);
    }

    @Override
    @JsonIgnore
    public String getRemoveStmt() {
        return "DELETE FROM EAN_number WHERE ID=?";
    }
}

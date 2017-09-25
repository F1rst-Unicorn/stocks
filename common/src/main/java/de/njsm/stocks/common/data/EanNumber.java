package de.njsm.stocks.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class EanNumber extends Data implements SqlRemovable {

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
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.eanNumber(this, input);                                        
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

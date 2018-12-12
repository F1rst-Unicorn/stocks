package de.njsm.stocks.server.v1.internal.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
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
        return "DELETE FROM \"EAN_number\" WHERE \"ID\"=?";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EanNumber eanNumber = (EanNumber) o;
        return id == eanNumber.id &&
                identifiesFood == eanNumber.identifiesFood &&
                Objects.equals(eanCode, eanNumber.eanCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, eanCode, identifiesFood);
    }

    @Override
    public String toString() {
        return "EanNumber{" +
                "id=" + id +
                ", eanCode='" + eanCode + '\'' +
                ", identifiesFood=" + identifiesFood +
                ", id=" + id +
                '}';
    }
}
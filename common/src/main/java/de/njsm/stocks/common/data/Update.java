package de.njsm.stocks.common.data;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;
import de.njsm.stocks.common.data.visitor.VisitorException;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Update extends Data {

    public String table;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd hh:mm:ss")
    public Date lastUpdate;

    public Update(String table, Date lastUpdate) {
        this.table = table;
        this.lastUpdate = lastUpdate;
    }

    public Update() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) throws VisitorException {
        return visitor.update(this, input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Update update = (Update) o;

        if (!table.equals(update.table)) return false;
        return lastUpdate.equals(update.lastUpdate);
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + lastUpdate.hashCode();
        return result;
    }
}

package de.njsm.stocks.common.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.DEFAULT,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@XmlRootElement
public class ServerTicket extends Data implements SqlRemovable {

    public int id;

    public Date creationDate;

    public int deviceId;

    public String ticket;

    public ServerTicket(int id, Date creationDate, int deviceId, String ticket) {
        this.id = id;
        this.creationDate = creationDate;
        this.deviceId = deviceId;
        this.ticket = ticket;
    }

    public ServerTicket() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.serverTicket(this, input);
    }

    @Override
    public void fillRemoveStmt(PreparedStatement stmt) throws SQLException {
        stmt.setInt(1, deviceId);
    }

    @Override
    public String getRemoveStmt() {
        return "DELETE FROM Ticket WHERE belongs_device=?";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerTicket that = (ServerTicket) o;
        return id == that.id &&
                deviceId == that.deviceId &&
                Objects.equals(creationDate, that.creationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, creationDate, deviceId);
    }

    @Override
    public String toString() {
        return "Ticket (" + id + ", " + creationDate + ", " + deviceId + ")";
    }
}

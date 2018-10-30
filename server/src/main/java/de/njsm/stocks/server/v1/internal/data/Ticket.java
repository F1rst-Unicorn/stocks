package de.njsm.stocks.server.v1.internal.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.njsm.stocks.server.v1.internal.data.visitor.StocksDataVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.SecureRandom;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
@JsonIgnoreProperties({ "id" })
@XmlRootElement
public class Ticket extends Data {

    @JsonIgnore
    public static final int TICKET_LENGTH = 64;

    public int deviceId;
    public String ticket;
    public String pemFile;

    public Ticket(int deviceId, String ticket, String pemFile) {
        this.deviceId = deviceId;
        this.ticket = ticket;
        this.pemFile = pemFile;
    }

    public Ticket() {
    }

    @Override
    public <I, O> O accept(StocksDataVisitor<I, O> visitor, I input) {
        return visitor.ticket(this, input);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ticket ticket1 = (Ticket) o;

        if (deviceId != ticket1.deviceId) return false;
        if (!ticket.equals(ticket1.ticket)) return false;
        return pemFile.equals(ticket1.pemFile);
    }

    @Override
    public int hashCode() {
        int result = deviceId;
        result = 31 * result + ticket.hashCode();
        result = 31 * result + pemFile.hashCode();
        return result;
    }

    @JsonIgnore
    public static String generateTicket() {
        SecureRandom generator = new SecureRandom();
        byte[] content = new byte[TICKET_LENGTH];

        for (int i = 0; i < TICKET_LENGTH; i++){
            content[i] = getNextByte(generator);
        }

        return new String(content);
    }

    @JsonIgnore
    private static byte getNextByte(SecureRandom generator) {
        byte result;
        do {
            result = (byte) generator.nextInt();
        } while (!Character.isLetterOrDigit(result));
        return result;
    }
}

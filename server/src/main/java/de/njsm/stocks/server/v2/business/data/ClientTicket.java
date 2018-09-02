package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.SecureRandom;

@XmlRootElement
public class ClientTicket {

    @JsonIgnore
    public static final int TICKET_LENGTH = 64;

    public int deviceId;

    public String ticket;

    public String pemFile;

    public ClientTicket() {
    }

    public ClientTicket(int deviceId, String ticket, String pemFile) {
        this.deviceId = deviceId;
        this.ticket = ticket;
        this.pemFile = pemFile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientTicket ticket1 = (ClientTicket) o;

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

    @Override
    public String toString() {
        return "ClientTicket{" +
                "deviceId=" + deviceId +
                ", ticket='" + ticket + '\'' +
                ", pemFile='" + pemFile + '\'' +
                '}';
    }
}

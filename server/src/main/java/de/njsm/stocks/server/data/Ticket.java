package de.njsm.stocks.server.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.SecureRandom;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Ticket extends Data {

    @JsonIgnore
    public static final int TICKET_LENGTH = 64;

    public int deviceId;
    public String ticket;
    public String pemFile;

    public Ticket() {
    }

    public Ticket(int deviceId, String ticket, String pemFile) {
        this.deviceId = deviceId;
        this.ticket = ticket;
        this.pemFile = pemFile;
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

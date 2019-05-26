/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.common.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.njsm.stocks.common.data.visitor.StocksDataVisitor;

import javax.xml.bind.annotation.XmlRootElement;
import java.security.SecureRandom;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
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

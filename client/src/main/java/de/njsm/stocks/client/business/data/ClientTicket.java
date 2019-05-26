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

package de.njsm.stocks.client.business.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientTicket {

    public int deviceId;

    public String ticket;

    public String pemFile;

    public ClientTicket() {
    }

    public ClientTicket(int deviceId, String ticket) {
        this.deviceId = deviceId;
        this.ticket = ticket;
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

    @Override
    public String toString() {
        return "ClientTicket{" +
                "deviceId=" + deviceId +
                ", ticket='" + ticket + '\'' +
                ", pemFile='" + pemFile + '\'' +
                '}';
    }
}

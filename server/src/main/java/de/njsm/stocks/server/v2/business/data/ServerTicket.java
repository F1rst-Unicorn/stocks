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

package de.njsm.stocks.server.v2.business.data;

import java.util.Date;
import java.util.Objects;

public class ServerTicket {

    private final int id;

    private final Date creationDate;

    private final int deviceId;

    private final String ticket;

    public ServerTicket(int id, Date creationDate, int deviceId, String ticket) {
        this.id = id;
        this.creationDate = creationDate;
        this.deviceId = deviceId;
        this.ticket = ticket;
    }

    public int getId() {
        return id;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public String getTicket() {
        return ticket;
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
}

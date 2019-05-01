package de.njsm.stocks.server.v2.business.data;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE,
        creatorVisibility = JsonAutoDetect.Visibility.NONE)
public class NewDeviceTicket {

    public int deviceId;

    public String ticket;

    public NewDeviceTicket() {
    }

    public NewDeviceTicket(int deviceId, String ticket) {
        this.deviceId = deviceId;
        this.ticket = ticket;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NewDeviceTicket that = (NewDeviceTicket) o;

        if (deviceId != that.deviceId) return false;
        return ticket != null ? ticket.equals(that.ticket) : that.ticket == null;

    }

    @Override
    public int hashCode() {
        int result = deviceId;
        result = 31 * result + (ticket != null ? ticket.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "NewDeviceTicket{" +
                "deviceId=" + deviceId +
                ", ticket='" + ticket + '\'' +
                '}';
    }
}

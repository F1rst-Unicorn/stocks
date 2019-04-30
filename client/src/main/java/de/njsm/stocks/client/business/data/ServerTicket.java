package de.njsm.stocks.client.business.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@JsonIgnoreProperties(value = { "pemFile" })
public class ServerTicket {

    public int deviceId;

    public String ticket;

    public ServerTicket() {
    }

    public ServerTicket(int deviceId, String ticket) {
        this.deviceId = deviceId;
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "ServerTicket{" +
                "deviceId=" + deviceId +
                ", ticket='" + ticket + '\'' +
                '}';
    }
}

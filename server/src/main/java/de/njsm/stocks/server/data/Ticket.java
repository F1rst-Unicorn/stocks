package de.njsm.stocks.server.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;

@JsonSerialize(include= JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class Ticket {
    public int deviceId;
    public String ticket;
    public String pemFile;
}

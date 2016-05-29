package de.njsm.stocks.linux.client.data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
@XmlRootElement
public class FoodItem {
    public int id;
    public Date eatByDate;
    public int ofType;
    public int storedIn;
    public int registers;
    public int buys;
}

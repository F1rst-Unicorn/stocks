package de.njsm.stocks.backend.data;

import java.util.Date;

public class FoodItem {
    public int id;
    public Date eatByDate;
    public int ofType;
    public int storedIn;
    public int registers;
    public int buys;

    public FoodItem(int id, Date eatByDate, int ofType, int storedIn, int registers, int buys) {
        this.id = id;
        this.eatByDate = eatByDate;
        this.ofType = ofType;
        this.storedIn = storedIn;
        this.registers = registers;
        this.buys = buys;
    }
}

package de.njsm.stocks.server.v2.business.data;

import java.time.Instant;

public class BitemporalUnit extends BitemporalData implements Unit {

    private final String name;

    private final String abbreviation;

    public BitemporalUnit(int id, int version, Instant validTimeStart, Instant validTimeEnd, Instant transactionTimeStart, Instant transactionTimeEnd, int initiates, String name, String abbreviation) {
        super(id, version, validTimeStart, validTimeEnd, transactionTimeStart, transactionTimeEnd, initiates);
        this.name = name;
        this.abbreviation = abbreviation;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAbbreviation() {
        return abbreviation;
    }
}

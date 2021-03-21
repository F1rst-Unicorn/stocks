package de.njsm.stocks.server.v2.business.data;

public class UnitForGetting extends VersionedData implements Unit {

    private final String name;

    private final String abbreviation;

    public UnitForGetting(int id, int version, String name, String abbreviation) {
        super(id, version);
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

package de.njsm.stocks.server.v2.business.data;

public class UnitForRenaming extends VersionedData implements Versionable<Unit> {

    private final String name;

    private final String abbreviation;

    public UnitForRenaming(int id, int version, String name, String abbreviation) {
        super(id, version);
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }
}

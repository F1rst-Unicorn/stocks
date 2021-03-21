package de.njsm.stocks.server.v2.business.data;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UnitRecord;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetStep;

import static de.njsm.stocks.server.v2.db.jooq.Tables.UNIT;

public class UnitForInsertion implements Insertable<UnitRecord, Unit> {

    private final String name;

    private final String abbreviation;

    public UnitForInsertion(String name, String abbreviation) {
        this.name = name;
        this.abbreviation = abbreviation;
    }

    @Override
    public InsertOnDuplicateStep<UnitRecord> insertValue(InsertSetStep<UnitRecord> insertInto, Principals principals) {
        return insertInto.columns(UNIT.NAME, UNIT.ABBREVIATION, UNIT.INITIATES)
                .values(name, abbreviation, principals.getDid());
    }
}

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import org.jooq.Table;
import org.jooq.TableField;

import java.sql.Connection;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.EAN_NUMBER;


public class EanNumberHandler extends CrudDatabaseHandler<EanNumberRecord, EanNumber> {


    public EanNumberHandler(Connection connection,
                            String resourceIdentifier,
                            InsertVisitor<EanNumberRecord> visitor) {
        super(connection, resourceIdentifier, visitor);
    }

    @Override
    protected Table<EanNumberRecord> getTable() {
        return EAN_NUMBER;
    }

    @Override
    protected TableField<EanNumberRecord, Integer> getIdField() {
        return EAN_NUMBER.ID;
    }

    @Override
    protected TableField<EanNumberRecord, Integer> getVersionField() {
        return EAN_NUMBER.VERSION;
    }

    @Override
    protected Function<EanNumberRecord, EanNumber> getDtoMap() {
        return cursor -> new EanNumber(
                cursor.getId(),
                cursor.getVersion(),
                cursor.getNumber(),
                cursor.getIdentifies()
                );
    }

}

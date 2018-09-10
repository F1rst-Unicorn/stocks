package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.db.jooq.tables.records.EanNumberRecord;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.types.UInteger;

import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.Tables.EAN_NUMBER;


public class EanNumberHandler extends CrudDatabaseHandler<EanNumberRecord, EanNumber> {


    public EanNumberHandler(ConnectionFactory connectionFactory,
                            String resourceIdentifier,
                            InsertVisitor<EanNumberRecord> visitor) {
        super(connectionFactory, resourceIdentifier, visitor);
    }

    @Override
    protected Table<EanNumberRecord> getTable() {
        return EAN_NUMBER;
    }

    @Override
    protected TableField<EanNumberRecord, UInteger> getIdField() {
        return EAN_NUMBER.ID;
    }

    @Override
    protected TableField<EanNumberRecord, UInteger> getVersionField() {
        return EAN_NUMBER.VERSION;
    }

    @Override
    protected Function<EanNumberRecord, EanNumber> getDtoMap() {
        return cursor -> new EanNumber(
                cursor.getId().intValue(),
                cursor.getVersion().intValue(),
                cursor.getNumber(),
                cursor.getIdentifies().intValue()
                );
    }

}

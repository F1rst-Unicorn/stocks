package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.VersionedData;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.types.UInteger;

public abstract class CrudRenameDatabaseHandler<T extends UpdatableRecord<T>, R extends VersionedData> extends CrudDatabaseHandler<T, R> {


    public CrudRenameDatabaseHandler(ConnectionFactory connectionFactory,
                                     String resourceIdentifier,
                                     InsertVisitor<T> visitor) {
        super(connectionFactory, resourceIdentifier, visitor);
    }

    public StatusCode rename(R item, String newName) {
        return runCommand(context -> {
            if (isMissing(item.id, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.update(getTable())
                    .set(getNameColumn(), newName)
                    .set(getVersionField(), getVersionField().add(1))
                    .where(getIdField().eq(UInteger.valueOf(item.id))
                            .and(getVersionField().eq(UInteger.valueOf(item.version))))
                    .execute();

            if (changedItems == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.INVALID_DATA_VERSION;

        });
    }

    protected abstract TableField<T, String> getNameColumn();
}

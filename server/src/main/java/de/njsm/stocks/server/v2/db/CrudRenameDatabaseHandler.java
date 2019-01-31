package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.VersionedData;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;

import java.sql.Connection;

public abstract class CrudRenameDatabaseHandler<T extends UpdatableRecord<T>, R extends VersionedData> extends CrudDatabaseHandler<T, R> {


    public CrudRenameDatabaseHandler(Connection connection,
                                     String resourceIdentifier,
                                     InsertVisitor<T> visitor) {
        super(connection, resourceIdentifier, visitor);
    }

    public StatusCode rename(R item, String newName) {
        return runCommand(context -> {
            if (isMissing(item, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.update(getTable())
                    .set(getNameColumn(), newName)
                    .set(getVersionField(), getVersionField().add(1))
                    .where(getIdField().eq(item.id)
                            .and(getVersionField().eq(item.version)))
                            .and(getVersionField().eq(item.version))
                    .execute();

            if (changedItems == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.INVALID_DATA_VERSION;

        });
    }

    protected abstract TableField<T, String> getNameColumn();
}

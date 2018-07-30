package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.VersionedData;
import fj.data.Validation;
import org.jooq.DSLContext;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UpdatableRecord;
import org.jooq.types.UInteger;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CrudDatabaseHandler<T extends UpdatableRecord<T>, R extends VersionedData> extends FailSafeDatabaseHandler {

    private InsertVisitor<T> visitor;

    public CrudDatabaseHandler(String url, String username, String password, String resourceIdentifier, InsertVisitor<T> visitor) {
        super(url, username, password, resourceIdentifier);
        this.visitor = visitor;
    }

    public StatusCode add(R item) {
        return runCommand(context -> {
            visitor.visit(item, context.insertInto(getTable()))
                    .execute();
            return StatusCode.SUCCESS;
        });

    }

    public Validation<StatusCode, List<R>> get() {
        return runQuery(context -> {
            List<R> result = context
                    .selectFrom(getTable())
                    .fetch()
                    .stream()
                    .map(getDtoMap())
                    .collect(Collectors.toList());

            return Validation.success(result);
        });
    }

    public StatusCode delete(R item) {
        return runCommand(context -> {
            if (isMissing(item.id, context))
                return StatusCode.NOT_FOUND;

            int changedItems = context.deleteFrom(getTable())
                    .where(getIdField().eq(UInteger.valueOf(item.id))
                            .and(getVersionField().eq(UInteger.valueOf(item.version))))
                    .execute();

            if (changedItems == 1)
                return StatusCode.SUCCESS;
            else
                return StatusCode.INVALID_DATA_VERSION;
        });
    }

    protected abstract Table<T> getTable();

    protected abstract Function<T, R> getDtoMap();

    protected abstract TableField<T, UInteger> getIdField();

    protected abstract TableField<T, UInteger> getVersionField();

    protected boolean isMissing(int id, DSLContext context) {
        int count = context.selectCount()
                .from(getTable())
                .where(getIdField().eq(UInteger.valueOf(id)))
                .fetch()
                .get(0)
                .value1();

        return count == 0;
    }
}

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.VersionedData;
import fj.data.Validation;
import org.jooq.*;
import org.jooq.types.UInteger;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class CrudDatabaseHandler<T extends UpdatableRecord<T>, R extends VersionedData>
        extends FailSafeDatabaseHandler
        implements PresenceChecker<R> {

    private InsertVisitor<T> visitor;

    public CrudDatabaseHandler(ConnectionFactory connectionFactory,
                               String resourceIdentifier,
                               InsertVisitor<T> visitor) {
        super(connectionFactory, resourceIdentifier);
        this.visitor = visitor;
    }

    public Validation<StatusCode, Integer> add(R item) {
        return runFunction(context -> {
            int lastInsertId = visitor.visit(item, context.insertInto(getTable()))
                    .returning(getIdField())
                    .fetch()
                    .getValue(0, getIdField())
                    .intValue();
            return Validation.success(lastInsertId);
        });

    }

    public Validation<StatusCode, List<R>> get() {
        return runFunction(context -> {
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
            if (isMissing(item, context))
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

    @Override
    public boolean isMissing(R item, DSLContext context) {
        int count = context.selectCount()
                .from(getTable())
                .where(getIdField().eq(UInteger.valueOf(item.id)))
                .fetch()
                .get(0)
                .value1();

        return count == 0;
    }

    protected abstract Table<T> getTable();

    protected abstract Function<T, R> getDtoMap();

    protected abstract TableField<T, UInteger> getIdField();

    protected abstract TableField<T, UInteger> getVersionField();
}

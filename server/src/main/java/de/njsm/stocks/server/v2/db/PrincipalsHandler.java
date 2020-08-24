package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;
import org.jooq.Field;
import org.jooq.impl.DSL;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;
import static de.njsm.stocks.server.v2.db.jooq.tables.UserDevice.USER_DEVICE;
import static de.njsm.stocks.server.v2.db.jooq.tables.Ticket.TICKET;

public class PrincipalsHandler extends FailSafeDatabaseHandler {

    public PrincipalsHandler(ConnectionFactory connectionFactory, String resourceIdentifier, int timeout) {
        super(connectionFactory, resourceIdentifier, timeout);
    }

    public Validation<StatusCode, Set<Principals>> getPrincipals() {
        return runFunction(context -> {
            Field<OffsetDateTime> now = DSL.currentOffsetDateTime();
            Set<Principals> result = context
                    .selectFrom(USER.join(USER_DEVICE)
                            .on(USER.ID.eq(USER_DEVICE.BELONGS_TO)))
                    .where(USER_DEVICE.ID.notIn(context.select(TICKET.BELONGS_DEVICE)
                                                .from(TICKET))
                            .and(USER.VALID_TIME_START.lessOrEqual(now))
                            .and(now.lessThan(USER.VALID_TIME_END))
                            .and(USER.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                            .and(USER_DEVICE.VALID_TIME_START.lessOrEqual(now))
                            .and(now.lessThan(USER_DEVICE.VALID_TIME_END))
                            .and(USER_DEVICE.TRANSACTION_TIME_END.eq(CrudDatabaseHandler.INFINITY))
                    )
                    .fetch()
                    .stream()
                    .map(record -> new Principals(
                            record.get(USER.NAME),
                            record.get(USER_DEVICE.NAME),
                            record.get(USER.ID),
                            record.get(USER_DEVICE.ID)
                    ))
                    .collect(Collectors.toSet());

            return Validation.success(result);
        });
    }
}

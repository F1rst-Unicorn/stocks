package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;

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
            Set<Principals> result = context
                    .selectFrom(USER.join(USER_DEVICE)
                            .on(USER.ID.eq(USER_DEVICE.BELONGS_TO)))
                    .where(USER_DEVICE.ID.notIn(context.select(TICKET.BELONGS_DEVICE)
                                                .from(TICKET)))
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

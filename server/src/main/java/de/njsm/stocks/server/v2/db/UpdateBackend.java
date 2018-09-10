package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Update;
import fj.data.Validation;

import java.util.List;
import java.util.stream.Collectors;

import static de.njsm.stocks.server.v2.db.jooq.Tables.UPDATES;

public class UpdateBackend extends FailSafeDatabaseHandler {

    public UpdateBackend(ConnectionFactory connectionFactory,
                         String resourceIdentifier) {
        super(connectionFactory, resourceIdentifier);
    }

    public Validation<StatusCode, List<Update>> getUpdates() {
        return runQuery(context -> {
            List<Update> dbResult = context.selectFrom(UPDATES)
                    .fetch()
                    .stream()
                    .map(record -> new Update(
                            record.getTableName(),
                            record.getLastUpdate().toInstant()))
                    .collect(Collectors.toList());
            return Validation.success(dbResult);
        });
    }
}

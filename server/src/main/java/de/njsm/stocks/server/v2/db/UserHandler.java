package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.User;
import de.njsm.stocks.server.v2.db.jooq.tables.records.UserRecord;
import org.jooq.Table;
import org.jooq.TableField;

import java.sql.Connection;
import java.util.function.Function;

import static de.njsm.stocks.server.v2.db.jooq.tables.User.USER;

public class UserHandler extends CrudDatabaseHandler<UserRecord, User> {


    public UserHandler(Connection connection,
                       String resourceIdentifier,
                       InsertVisitor<UserRecord> visitor) {
        super(connection, resourceIdentifier, visitor);
    }

    @Override
    protected Table<UserRecord> getTable() {
        return USER;
    }

    @Override
    protected Function<UserRecord, User> getDtoMap() {
        return record -> new User(
                record.getId(),
                record.getVersion(),
                record.getName()
        );
    }

    @Override
    protected TableField<UserRecord, Integer> getIdField() {
        return USER.ID;
    }

    @Override
    protected TableField<UserRecord, Integer> getVersionField() {
        return USER.VERSION;
    }
}

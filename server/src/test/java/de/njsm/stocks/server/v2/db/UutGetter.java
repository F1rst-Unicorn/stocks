package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import org.jooq.TableRecord;

public interface UutGetter<T extends TableRecord<T>, N extends Entity<N>> {
    CrudDatabaseHandler<T, N> getDbHandler();
}

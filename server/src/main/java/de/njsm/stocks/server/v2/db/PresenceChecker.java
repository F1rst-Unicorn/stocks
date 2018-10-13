package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.server.v2.business.data.VersionedData;
import org.jooq.DSLContext;

public interface PresenceChecker<R extends VersionedData> {

    boolean isMissing(R item, DSLContext context);
}

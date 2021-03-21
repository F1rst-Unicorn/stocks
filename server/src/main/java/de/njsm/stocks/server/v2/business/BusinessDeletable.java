package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.business.data.Versionable;
import de.njsm.stocks.server.v2.business.json.PrincipalCarrier;

public interface BusinessDeletable<T extends Versionable<U>, U extends Entity<U>> extends PrincipalCarrier {
    StatusCode delete(T entity);
}

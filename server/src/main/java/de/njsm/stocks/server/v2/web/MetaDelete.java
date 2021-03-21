package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.BusinessDeletable;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.business.data.Versionable;
import de.njsm.stocks.server.v2.web.data.Response;
import org.glassfish.jersey.internal.util.Producer;

import javax.servlet.http.HttpServletRequest;

import static de.njsm.stocks.server.v2.web.Endpoint.*;

public interface MetaDelete<T extends Versionable<U>, U extends Entity<U>> {

    default Response delete(HttpServletRequest request,
                            Producer<T> producer) {
        getManager().setPrincipals(getPrincipals(request));
        StatusCode status = getManager().delete(producer.call());
        return new Response(status);
    }

    BusinessDeletable<T, U> getManager();
}

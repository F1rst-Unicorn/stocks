package de.njsm.stocks.server.v2.web;

import de.njsm.stocks.server.v2.business.BusinessGettable;
import de.njsm.stocks.server.v2.business.StatusCode;
import de.njsm.stocks.server.v2.business.data.Entity;
import de.njsm.stocks.server.v2.web.data.Response;
import de.njsm.stocks.server.v2.web.data.StreamResponse;
import fj.data.Validation;
import org.jooq.TableRecord;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Optional;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.web.Endpoint.parseToInstant;

public interface Get<U extends TableRecord<U>, T extends Entity<T>> {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    default void get(@Suspended AsyncResponse response,
                    @QueryParam("bitemporal") int bitemporalParameter,
                    @QueryParam("startingFrom") String startingFromParameter) {
        boolean bitemporal = bitemporalParameter == 1;
        Optional<Instant> startingFrom = parseToInstant(startingFromParameter, "startingFrom");
        if (startingFrom.isPresent()) {
            Validation<StatusCode, Stream<T>> result = getManager().get(response, bitemporal, startingFrom.get());
            response.resume(new StreamResponse<>(result));
        } else {
            response.resume(new Response(StatusCode.INVALID_ARGUMENT));
        }
    }

    BusinessGettable<U, T> getManager();
}

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.StatusCode;
import fj.data.Validation;
import org.jooq.TableRecord;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static de.njsm.stocks.server.v2.matchers.Matchers.matchesInsertable;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public interface EntityDbTestCase<T extends TableRecord<T>, N extends Entity<N>> extends UutGetter<T, N> {

    default void assertInsertableIsInserted(Validation<StatusCode, Integer> result,
                                            Insertable<N> data,
                                            int expectedId,
                                            int expectedNumberOfEntities) {
        assertTrue(result.isSuccess());
        assertEquals(Integer.valueOf(expectedId), result.success());

        Validation<StatusCode, Stream<N>> units = getDbHandler().get(false, Instant.EPOCH);
        assertTrue(units.isSuccess());

        List<N> list = units.success().collect(Collectors.toList());
        assertEquals(expectedNumberOfEntities, list.size());
        assertThat(list, hasItem(matchesInsertable(data)));
    }
}

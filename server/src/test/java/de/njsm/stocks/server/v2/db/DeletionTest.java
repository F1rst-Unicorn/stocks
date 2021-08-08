package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.common.api.Versionable;
import org.jooq.TableRecord;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.njsm.stocks.server.v2.matchers.Matchers.*;
import static de.njsm.stocks.server.v2.web.PrincipalFilterTest.TEST_USER;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public interface DeletionTest<T extends TableRecord<T>, N extends Entity<N>> extends EntityDbTestCase<T, N>, SampleDataInformer {

    @Test
    default void deletingSuccessfullyWorks() {
        Versionable<N> data = getValidEntity();

        StatusCode result = getDbHandler().delete(data);

        assertEquals(StatusCode.SUCCESS, result);
        List<N> currentEntities = getData();
        assertThat(currentEntities, not(hasItem(matchesVersionableExactly(data))));
        assertThat(currentEntities.size(), equalTo(getNumberOfEntities() - 1));

        assertThat(getBitemporalData(), hasItem(allOf(
                matchesVersionableExactly(data),
                isTerminatedForInfinity(),
                wasInitiatedBy(TEST_USER)
        )));
    }

    @Test
    default void deletingInvalidDataVersionIsRejected() {
        Versionable<N> data = getWrongVersionEntity();

        StatusCode result = getDbHandler().delete(data);

        assertEquals(StatusCode.INVALID_DATA_VERSION, result);
        assertThat(getData().size(), equalTo(getNumberOfEntities()));
    }

    @Test
    default void deletingUnknownEntityIsRejected() {
        Versionable<N> data = getUnknownEntity();

        StatusCode result = getDbHandler().delete(data);

        assertEquals(StatusCode.NOT_FOUND, result);
        assertThat(getData().size(), equalTo(getNumberOfEntities()));
    }

    Versionable<N> getUnknownEntity();

    Versionable<N> getWrongVersionEntity();

    Versionable<N> getValidEntity();
}

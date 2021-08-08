package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import de.njsm.stocks.common.api.Insertable;
import de.njsm.stocks.common.api.StatusCode;
import fj.data.Validation;
import org.jooq.TableRecord;
import org.junit.jupiter.api.Test;

public interface InsertionTest<T extends TableRecord<T>, N extends Entity<N>> extends EntityDbTestCase<T, N>, SampleDataInformer {

    @Test
    default void testInsertion() {
        Insertable<N> data = getInsertable();

        Validation<StatusCode, Integer> code = getDbHandler().addReturningId(data);

        assertInsertableIsInserted(code, data, getNumberOfEntities() + 1, getNextId());
    }

    Insertable<N> getInsertable();
}

package de.njsm.stocks.server.v2.db;

import de.njsm.stocks.common.api.Entity;
import org.jooq.TableRecord;

public interface CrudOperationsTest<T extends TableRecord<T>, N extends Entity<N>> extends InsertionTest<T, N>, DeletionTest<T, N> {}

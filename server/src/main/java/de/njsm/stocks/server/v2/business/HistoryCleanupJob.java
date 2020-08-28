package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.db.CrudDatabaseHandler;
import de.njsm.stocks.server.v2.db.TransactionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Period;
import java.util.List;

public class HistoryCleanupJob {

    private static final Logger LOG = LogManager.getLogger(HistoryCleanupJob.class);

    private final Period maxHistory;

    private final TransactionHandler transactionHandler;

    private final List<CrudDatabaseHandler<?, ?>> tableHandlers;

    public HistoryCleanupJob(Period maxHistory, TransactionHandler transactionHandler, List<CrudDatabaseHandler<?, ?>> tableHandlers) {
        this.maxHistory = maxHistory;
        this.transactionHandler = transactionHandler;
        this.tableHandlers = tableHandlers;
    }

    public void run() {
        LOG.debug("Started");
        StatusCode result;

        for (CrudDatabaseHandler<?, ?> h : tableHandlers) {
            result = h.cleanDataOlderThan(maxHistory);

            if (result.isFail()) {
                LOG.warn("Cleaning history on {} failed with {}", h, result);
                result = transactionHandler.rollback();

                if (result.isFail()) {
                    LOG.error("Rollback failed with {}", result);
                }

                return;
            }
        }

        result = transactionHandler.commit();
        if (result.isFail()) {
            LOG.warn("Commit failed with {}", result);
        }

        LOG.debug("Stopped");
    }
}

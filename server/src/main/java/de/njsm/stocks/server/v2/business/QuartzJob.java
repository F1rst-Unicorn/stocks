/* stocks is client-server program to manage a household's food stock
 * Copyright (C) 2019  The stocks developers
 *
 * This file is part of the stocks program suite.
 *
 * stocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * stocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.PrincipalsHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class QuartzJob {

    private static final Logger LOG = LogManager.getLogger(QuartzJob.class);

    private final PrincipalsHandler dbHandler;

    private Principals principals;

    protected QuartzJob(PrincipalsHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public void runJob() {
        LOG.debug("Started " + this.getClass().getSimpleName());
        initialiseDbHandlers()
                .bind(this::run)
                .map(this::finaliseDbOperation);
        LOG.debug("Stopped " + this.getClass().getSimpleName());
    }

    private StatusCode initialiseDbHandlers() {
        Validation<StatusCode, Principals> jobRunnerPrincipal = dbHandler.getJobRunnerPrincipal();
        jobRunnerPrincipal.foreachDoEffect(this::setPrincipals);
        return StatusCode.asCode(jobRunnerPrincipal);
    }

    private void setPrincipals(Principals principals) {
        this.principals = principals;
        setPrincipalsOnDbHandlers(principals);
    }

    protected abstract StatusCode run();

    protected abstract void setPrincipalsOnDbHandlers(Principals principals);

    protected Principals getPrincipals() {
        return principals;
    }

    private StatusCode finaliseDbOperation(StatusCode businessResult) {
        StatusCode result;
        if (businessResult.isSuccess()) {
            result = dbHandler.commit();
        } else {
            LOG.error("Job failed with " + businessResult);
            LOG.error("DB is rolled back");
            result = dbHandler.rollback();
        }

        if (result.isFail()) {
            LOG.error("Failed to finalise db operation with " + result);
        }
        return result;
    }
}

/*
 * stocks is client-server program to manage a household's food stock
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
 *
 */

package de.njsm.stocks.server.v2.business.job;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.PrincipalsHandler;
import de.njsm.stocks.server.v2.web.security.StocksAuthentication;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

public abstract class QuartzJob {

    private static final Logger LOG = LogManager.getLogger(QuartzJob.class);

    private final PrincipalsHandler dbHandler;

    private final AuthenticationManager authenticationManager;

    protected QuartzJob(PrincipalsHandler dbHandler, AuthenticationManager authenticationManager) {
        this.dbHandler = dbHandler;
        this.authenticationManager = authenticationManager;
    }

    public void execute() {
        LOG.debug("Started " + this.getClass().getSimpleName());
        initialiseDbHandlers()
                .bind(this::run)
                .map(this::finaliseDbOperation);
        LOG.debug("Stopped " + this.getClass().getSimpleName());
    }

    private StatusCode initialiseDbHandlers() {
        Validation<StatusCode, Principals> jobRunnerPrincipal = dbHandler.getJobRunnerPrincipal();
        jobRunnerPrincipal.foreachDoEffect(principals -> {
            var result = authenticationManager.authenticate(new StocksAuthentication(principals));
            if (result.isAuthenticated()) {
                SecurityContextHolder.getContext().setAuthentication(result);
            }
        });
        return StatusCode.asCode(jobRunnerPrincipal);
    }

    protected abstract StatusCode run();

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

    protected Principals getPrincipals() {
        return (Principals) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

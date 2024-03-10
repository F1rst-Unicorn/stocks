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

package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.FoodItemHandler;
import de.njsm.stocks.server.v2.db.PrincipalsHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

import static de.njsm.stocks.common.api.StatusCode.asCode;
import static fj.Semigroup.firstSemigroup;

public class CaConsistencyCheckJob extends QuartzJob {

    private static final Logger LOG = LogManager.getLogger(CaConsistencyCheckJob.class);

    private final AuthAdmin authAdmin;

    private final PrincipalsHandler dbHandler;

    private final UserDeviceHandler userDeviceHandler;

    private final FoodItemHandler foodItemHandler;

    public CaConsistencyCheckJob(AuthAdmin authAdmin, PrincipalsHandler dbHandler, UserDeviceHandler userDeviceHandler, FoodItemHandler foodItemHandler) {
        super(dbHandler);
        this.authAdmin = authAdmin;
        this.dbHandler = dbHandler;
        this.userDeviceHandler = userDeviceHandler;
        this.foodItemHandler = foodItemHandler;
    }

    public StatusCode run() {
        return asCode(
                dbHandler.getPrincipals().accumulate(firstSemigroup(), authAdmin.getValidPrincipals(), (dbPrincipals, caPrincipals) -> {
                    revokeDevicesWithoutDbEntry(caPrincipals, dbPrincipals);
                    removeRevokedDevicesFromDb(caPrincipals, dbPrincipals);
                    return StatusCode.SUCCESS;
                })
        );
    }

    @Override
    protected void setPrincipalsOnDbHandlers(Principals principals) {
        dbHandler.setPrincipals(principals);
        userDeviceHandler.setPrincipals(principals);
        foodItemHandler.setPrincipals(principals);
    }

    private void revokeDevicesWithoutDbEntry(Set<Principals> caPrincipals, Set<Principals> dbPrincipals) {
        Set<Principals> toRevoke = new HashSet<>(caPrincipals);
        toRevoke.removeAll(dbPrincipals);

        for (Principals p : toRevoke) {
            StatusCode result = authAdmin.revokeCertificate(p.getDid());
            if (result == StatusCode.SUCCESS) {
                LOG.info("Revoked access of formerly deleted device {} a.k.a. {}", p.getReadableString(), p);
            } else {
                LOG.error("Failed to revoke {} a.k.a. {}", p.getReadableString(), p);
            }
        }
    }

    private void removeRevokedDevicesFromDb(Set<Principals> caPrincipals, Set<Principals> dbPrincipals) {
        Set<Principals> toRemove = new HashSet<>(dbPrincipals);
        toRemove.removeAll(caPrincipals);

        for (Principals p : toRemove) {
            StatusCode result = foodItemHandler.transferFoodItems(p.toDevice(), getPrincipals().toDevice())
                    .bind(() -> userDeviceHandler.delete(p.toDevice()));
            if (result == StatusCode.SUCCESS) {
                LOG.info("Removed revoked device {} a.k.a. {} from DB", p.getReadableString(), p);
            } else {
                LOG.error("Failed to remove revoked device {} a.k.a. {} from DB", p.getReadableString(), p);
            }
        }
    }
}

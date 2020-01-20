package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.util.AuthAdmin;
import de.njsm.stocks.server.util.Principals;
import de.njsm.stocks.server.v2.db.PrincipalsHandler;
import de.njsm.stocks.server.v2.db.UserDeviceHandler;
import fj.data.Validation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class CaConsistencyCheckJob {

    private static final Logger LOG = LogManager.getLogger(CaConsistencyCheckJob.class);

    private AuthAdmin authAdmin;

    private PrincipalsHandler dbHandler;

    private UserDeviceHandler deviceHandler;

    public CaConsistencyCheckJob(AuthAdmin authAdmin, PrincipalsHandler dbHandler, UserDeviceHandler deviceHandler) {
        this.authAdmin = authAdmin;
        this.dbHandler = dbHandler;
        this.deviceHandler = deviceHandler;
    }

    public void run() {
        LOG.debug("Started");

        Validation<StatusCode, Set<Principals>> dbResult = dbHandler.getPrincipals();
        if (dbResult.isFail()) {
            LOG.error("Failed on DB: {}", dbResult.fail());
            dbHandler.rollback();
            return;
        }

        Validation<StatusCode, Set<Principals>> caResult = authAdmin.getValidPrincipals();
        if (caResult.isFail()) {
            LOG.error("Failed on CA: {}", caResult.fail());
            dbHandler.rollback();
            return;
        }

        Set<Principals> caPrincipals = caResult.success();
        Set<Principals> dbPrincipals = dbResult.success();

        revokeDevicesWithoutDbEntry(caPrincipals, dbPrincipals);
        removeRevokedDevicesFromDb(caPrincipals, dbPrincipals);

        dbHandler.commit();
        LOG.debug("Stopped");
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
            StatusCode result = deviceHandler.delete(p.toDevice());
            if (result == StatusCode.SUCCESS) {
                LOG.info("Removed revoked device {} a.k.a. {} from DB", p.getReadableString(), p);
            } else {
                LOG.error("Failed to remove revoked device {} a.k.a. {} from DB", p.getReadableString(), p);
            }
        }
    }
}

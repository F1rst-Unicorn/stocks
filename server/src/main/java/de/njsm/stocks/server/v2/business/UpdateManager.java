package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.Update;
import de.njsm.stocks.server.v2.db.UpdateBackend;
import fj.data.Validation;

import java.util.List;

public class UpdateManager extends BusinessObject {

    private UpdateBackend updateBackend;

    public UpdateManager(UpdateBackend updateBackend) {
        super(updateBackend);
        this.updateBackend = updateBackend;
    }

    public Validation<StatusCode, List<Update>> getUpdates() {
        return runFunction(() -> {
            updateBackend.setReadOnly();
            return updateBackend.getUpdates();
        });
    }
}

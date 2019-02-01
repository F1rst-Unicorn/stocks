package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import fj.data.Validation;

import java.util.List;

public class EanNumberManager extends BusinessObject {

    private EanNumberHandler eanNumberHandler;

    public EanNumberManager(EanNumberHandler eanNumberHandler) {
        super(eanNumberHandler);
        this.eanNumberHandler = eanNumberHandler;
    }

    public Validation<StatusCode, Integer> add(EanNumber item) {
        return runFunction(() -> eanNumberHandler.add(item));
    }

    public Validation<StatusCode, List<EanNumber>> get() {
        return runFunction(() -> {
            eanNumberHandler.setReadOnly();
            return eanNumberHandler.get();
        });
    }

    public StatusCode delete(EanNumber item) {
        return runOperation(() -> eanNumberHandler.delete(item));
    }
}

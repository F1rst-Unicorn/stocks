package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.business.data.EanNumber;
import de.njsm.stocks.server.v2.db.EanNumberHandler;
import fj.data.Validation;

import java.util.List;

public class EanNumberManager extends BusinessObject {

    private EanNumberHandler eanNumberHandler;

    public EanNumberManager(EanNumberHandler eanNumberHandler) {
        this.eanNumberHandler = eanNumberHandler;
    }

    public Validation<StatusCode, Integer> add(EanNumber item) {
        return finishTransaction(eanNumberHandler.add(item), eanNumberHandler);
    }

    public Validation<StatusCode, List<EanNumber>> get() {
        return finishTransaction(eanNumberHandler.get(), eanNumberHandler);
    }

    public StatusCode delete(EanNumber item) {
        return finishTransaction(eanNumberHandler.delete(item), eanNumberHandler);
    }
}

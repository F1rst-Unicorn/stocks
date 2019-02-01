package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;
import org.glassfish.jersey.internal.util.Producer;

public class BusinessObject {

    private FailSafeDatabaseHandler dbHandler;

    public BusinessObject(FailSafeDatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    <O> Validation<StatusCode, O> runFunction(Producer<Validation<StatusCode, O>> operation) {
        Validation<StatusCode, O> result;
        do {
            result = operation.call();
        } while (result.isFail() && result.fail() == StatusCode.SERIALISATION_CONFLICT);
        return finishTransaction(result);
    }

    StatusCode runOperation(Producer<StatusCode> operation) {
        StatusCode result;
        do {
            result = operation.call();
        } while (result == StatusCode.SERIALISATION_CONFLICT);
        return finishTransaction(result);
    }

    <O> Validation<StatusCode, O> finishTransaction(Validation<StatusCode, O> carry) {
        if (carry.isFail()) {
            dbHandler.rollback();
            return carry;
        } else {
            StatusCode next = dbHandler.commit();
            if (next == StatusCode.SUCCESS) {
                return carry;
            } else {
                return Validation.fail(next);
            }
        }
    }

    StatusCode finishTransaction(StatusCode carry) {
        if (carry != StatusCode.SUCCESS) {
            dbHandler.rollback();
            return carry;
        } else {
            StatusCode next = dbHandler.commit();
            if (next == StatusCode.SUCCESS) {
                return StatusCode.SUCCESS;
            } else {
                return next;
            }
        }
    }


}

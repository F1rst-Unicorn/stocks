package de.njsm.stocks.server.v2.business;

import de.njsm.stocks.server.v2.db.FailSafeDatabaseHandler;
import fj.data.Validation;

public class BusinessObject {

    <O> Validation<StatusCode, O> finishTransaction(Validation<StatusCode, O> carry, FailSafeDatabaseHandler db) {
        if (carry.isFail()) {
            db.rollback();
            return carry;
        } else {
            StatusCode next = db.commit();
            if (next == StatusCode.SUCCESS) {
                return carry;
            } else {
                return Validation.fail(next);
            }
        }
    }

    StatusCode finishTransaction(StatusCode carry, FailSafeDatabaseHandler db) {
        if (carry != StatusCode.SUCCESS) {
            db.rollback();
            return carry;
        } else {
            StatusCode next = db.commit();
            if (next == StatusCode.SUCCESS) {
                return StatusCode.SUCCESS;
            } else {
                return next;
            }
        }
    }


}

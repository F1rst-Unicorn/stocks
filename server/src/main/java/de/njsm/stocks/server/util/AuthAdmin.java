package de.njsm.stocks.server.util;

import de.njsm.stocks.server.v2.business.StatusCode;
import fj.data.Validation;

public interface AuthAdmin {

    StatusCode saveCsr(int deviceId, String content);

    Validation<StatusCode, String> getCertificate(int deviceId);

    void wipeDeviceCredentials(int deviceId);

    StatusCode generateCertificate(int deviceId);

    Validation<StatusCode, Principals> getPrincipals(int deviceId);

    StatusCode revokeCertificate(int id);

}
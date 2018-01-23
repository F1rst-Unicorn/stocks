package de.njsm.stocks.server.internal.auth;

import de.njsm.stocks.common.data.Principals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MockAuthAdmin implements AuthAdmin {

    protected List<Integer> revokedIds;

    public MockAuthAdmin() {
        revokedIds = new ArrayList<>();
    }

    @Override
    public void saveCsr(int deviceId, String content) throws IOException {

    }

    @Override
    public String getCertificate(int deviceId) throws IOException {
        return null;
    }

    @Override
    public void wipeDeviceCredentials(int deviceId) {

    }

    @Override
    public void generateCertificate(int deviceId) throws IOException {

    }

    @Override
    public Principals getPrincipals(int deviceId) throws IOException {
        return null;
    }

    @Override
    public void revokeCertificate(int id) {
        revokedIds.add(id);
    }

    public List<Integer> getRevokedIds() {
        return revokedIds;
    }

}

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
    public void generateCertificate(String userFile) throws IOException {

    }

    @Override
    public Principals getPrincipals(String csrFile) throws IOException {
        return new Principals("Jack", "Device", 1, 1);
    }

    @Override
    public void revokeCertificate(int id) {
        revokedIds.add(id);
    }

    public List<Integer> getRevokedIds() {
        return revokedIds;
    }

}

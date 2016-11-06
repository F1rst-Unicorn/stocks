package de.njsm.stocks.server.internal.auth;

import java.util.ArrayList;
import java.util.List;

public class MockAuthAdmin implements AuthAdmin {

    protected List<Integer> revokedIds;

    public MockAuthAdmin() {
        revokedIds = new ArrayList<>();
    }

    @Override
    public void revokeCertificate(int id) {
        revokedIds.add(id);
    }

    public List<Integer> getRevokedIds() {
        return revokedIds;
    }

}

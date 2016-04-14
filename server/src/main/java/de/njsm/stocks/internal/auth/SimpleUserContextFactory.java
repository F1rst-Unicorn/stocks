package de.njsm.stocks.internal.auth;

import javax.servlet.http.HttpServletRequest;

public class SimpleUserContextFactory implements ContextFactory {

    public UserContext getUserContext(HttpServletRequest request) {
        return new UserContext("test_user", 0, "test_device", 0);
    }

}

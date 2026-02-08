package de.njsm.stocks.server.v2.web.security;

import de.njsm.stocks.server.util.Principals;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class StocksAuthentication implements Authentication {

	private final Principals principals;

	public StocksAuthentication(Principals principals) {
		this.principals = principals;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of();
	}

	@Override
	public Object getCredentials() {
		return null;
	}

	@Override
	public Object getDetails() {
		return null;
	}

	@Override
	public Principals getPrincipal() {
		return principals;
	}

	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
	}

	@Override
	public String getName() {
		return "";
	}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StocksAuthentication other)
            return Objects.equals(principals, other.principals);
        else
            return false;
    }

    @Override
    public int hashCode() {
        return principals.hashCode();
    }
}

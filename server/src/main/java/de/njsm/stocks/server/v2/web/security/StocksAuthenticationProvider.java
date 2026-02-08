package de.njsm.stocks.server.v2.web.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
class StocksAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		if (authentication instanceof StocksAuthentication) {
			return authentication;
		}
		throw new AuthenticationServiceException("Unsupported authentication type");
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication == StocksAuthentication.class;
	}
}

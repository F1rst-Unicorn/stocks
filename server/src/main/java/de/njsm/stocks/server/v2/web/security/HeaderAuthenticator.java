package de.njsm.stocks.server.v2.web.security;

import de.njsm.stocks.common.api.StatusCode;
import de.njsm.stocks.server.util.Principals;
import fj.data.Validation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class HeaderAuthenticator extends OncePerRequestFilter {

	private static final Logger LOG = LogManager.getLogger(HeaderAuthenticator.class);

	public static final String SSL_CLIENT_KEY = "X-SSL-Client-S-DN";

	public static final String ORIGIN = "X-ORIGIN";

	public static final String ORIGIN_SENTRY = "sentry";

	private final AuthenticationManager authenticationManager;

	public HeaderAuthenticator(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	@Override
	protected void doFilterInternal(
		HttpServletRequest requestContext, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		if (requestContext.getHeader(ORIGIN).equals(ORIGIN_SENTRY)) {
			LOG.info("Anonymous user " +
				requestContext.getMethod().toLowerCase() + "s " +
				requestContext.getServletPath());
            filterChain.doFilter(requestContext, response);
		} else {
			addPrincipals(requestContext, response, filterChain);
		}
	}

	private void addPrincipals(
		HttpServletRequest requestContext, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {
		String headerContent = requestContext.getHeader(SSL_CLIENT_KEY);
		Validation<StatusCode, Principals> principals = parseSubjectName(headerContent);

		if (principals.isFail()) {
			LOG.error("Got invalid request with SSL header '"
				+ SSL_CLIENT_KEY + ": " + headerContent);
			response.setStatus(Response.Status.FORBIDDEN.getStatusCode());
			return;
		}

		grantAccess(requestContext, response, filterChain, principals.success());
	}

	private void grantAccess(
		HttpServletRequest requestContext, HttpServletResponse response, FilterChain filterChain,
		Principals principals
	) throws ServletException, IOException {
		var result = authenticationManager.authenticate(new StocksAuthentication(principals));
		if (result.isAuthenticated()) {
			SecurityContextHolder.getContext().setAuthentication(result);
			LOG.info(principals.getReadableString() + " " +
				requestContext.getMethod().toLowerCase() + "s " +
				requestContext.getServletPath());
			filterChain.doFilter(requestContext, response);
		}
	}

	public static Validation<StatusCode, Principals> parseSubjectName(String subject) {
		LOG.debug("Parsing " + subject);
		subject = subject.trim();
		Validation<StatusCode, String> rawSubject = extractCommonName(subject);

		if (rawSubject.isFail()) {
			return Validation.fail(rawSubject.fail());
		}

		String commonName = rawSubject.success();

		int[] indices = new int[3];
		int lastIndex = -1;
		// find indices of the $ signs
		for (int i = 0; i < 3; i++) {
			indices[i] = commonName.indexOf('$', lastIndex + 1);
			lastIndex = indices[i];
			if (lastIndex == -1) {
				LOG.warn("client name '" + subject + "' is malformed");
				return Validation.fail(StatusCode.INVALID_ARGUMENT);
			}
		}

		return Validation.success(new Principals(commonName.substring(0, indices[0]),
			commonName.substring(indices[1] + 1, indices[2]),
			commonName.substring(indices[0] + 1, indices[1]),
			commonName.substring(indices[2] + 1)));

	}

	private static Validation<StatusCode, String> extractCommonName(String subject) {
		Pattern pattern = Pattern.compile(".*CN=([-_ a-zA-Z0-9$]*).*");
		Matcher matcher = pattern.matcher(subject);
		if (matcher.matches()) {
			return Validation.success(matcher.group(1));
		} else {
			LOG.warn("client name '" + subject + "' is malformed");
			return Validation.fail(StatusCode.INVALID_ARGUMENT);
		}
	}
}

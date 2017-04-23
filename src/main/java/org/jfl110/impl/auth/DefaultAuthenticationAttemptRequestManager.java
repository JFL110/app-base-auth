package org.jfl110.impl.auth;

import javax.servlet.ServletRequest;

import org.jfl110.api.auth.AuthenticationAttempt;
import org.jfl110.api.auth.AuthenticationAttemptRequestManager;

public class DefaultAuthenticationAttemptRequestManager implements AuthenticationAttemptRequestManager {

	private static final String REQUEST_ATTRIBUTE_KEY = "auth-attempt";

	@Override
	public void insertIntoRequest(ServletRequest request, AuthenticationAttempt authAttempt) {
		request.setAttribute(REQUEST_ATTRIBUTE_KEY, authAttempt);
	}

	@Override
	public AuthenticationAttempt retrieveFromRequest(ServletRequest request) {

		Object authAttempt = request.getAttribute(REQUEST_ATTRIBUTE_KEY);

		if (authAttempt == null) {
			throw new IllegalStateException("No auth attempt found in request, have you configured your auth filters?");
		}

		return (AuthenticationAttempt) authAttempt;
	}
}
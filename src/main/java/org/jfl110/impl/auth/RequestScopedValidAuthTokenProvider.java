package org.jfl110.impl.auth;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jfl110.api.auth.AuthTokenClaim;
import org.jfl110.api.auth.AuthTokenStatus;
import org.jfl110.api.auth.AuthenticationAttempt;

public class RequestScopedValidAuthTokenProvider implements Provider<AuthTokenClaim> {

	private final Provider<AuthenticationAttempt> authenticationAttempt;

	@Inject
	RequestScopedValidAuthTokenProvider(Provider<AuthenticationAttempt> authenticationAttempt) {
		this.authenticationAttempt = authenticationAttempt;
	}

	@Override
	public AuthTokenClaim get() {
		AuthenticationAttempt authAttempt = authenticationAttempt.get();
		
		if (!authAttempt.authToken().isPresent()  ) {
			throw new IllegalArgumentException("No token was in request, have you configured the filters correctly?");
		}

		if(authAttempt.status() != AuthTokenStatus.VALID){
			throw new IllegalArgumentException("Token was found but it was invalid, have you configured the filters correctly? Status was ["+authAttempt.status()+"]");
		}
		
		return authAttempt.authToken().get();
	}
}

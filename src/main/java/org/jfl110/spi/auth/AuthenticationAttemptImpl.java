package org.jfl110.spi.auth;

import org.jfl110.api.auth.AuthTokenClaim;
import org.jfl110.api.auth.AuthTokenStatus;
import org.jfl110.api.auth.AuthenticationAttempt;

import com.google.common.base.Optional;

public final class AuthenticationAttemptImpl implements AuthenticationAttempt {

	private final Optional<AuthTokenClaim> token;
	private final AuthTokenStatus status;

	public AuthenticationAttemptImpl(Optional<AuthTokenClaim> token, AuthTokenStatus status) {
		this.token = token;
		this.status = status;
	}

	@Override
	public Optional<AuthTokenClaim> authToken() {
		return token;
	}

	@Override
	public AuthTokenStatus status() {
		return status;
	}

}
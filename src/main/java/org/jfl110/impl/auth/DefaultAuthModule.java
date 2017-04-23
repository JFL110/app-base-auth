package org.jfl110.impl.auth;

import org.jfl110.api.auth.AuthTokenClaim;
import org.jfl110.api.auth.Authenticated;
import org.jfl110.api.auth.AuthenticationAttempt;
import org.jfl110.api.auth.AuthenticationAttemptRequestManager;
import org.jfl110.api.auth.MandatoryAuthFilter;
import org.jfl110.api.jsonexception.JsonExceptionFilter;

import com.google.inject.AbstractModule;

public class DefaultAuthModule extends AbstractModule{

	@Override
	protected void configure() {
		bind(MandatoryAuthFilter.class).to(MandatoryAuthFilterImpl.class);
		
		bind(AuthenticationAttemptRequestManager.class).to(DefaultAuthenticationAttemptRequestManager.class);
		
		// TODO move to a core something
		bind(JsonExceptionFilter.class).to(DefaultJsonExceptionFilter.class);
		
		bind(AuthTokenClaim.class).annotatedWith(Authenticated.class).toProvider(RequestScopedValidAuthTokenProvider.class);
		bind(AuthenticationAttempt.class).toProvider(RequestScopedAuthenticationAttemptProvider.class);
	}
}
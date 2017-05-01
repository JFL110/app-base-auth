package org.jfl110.impl.auth;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import org.jfl110.api.auth.AuthenticationAttempt;
import org.jfl110.api.auth.AuthenticationAttemptRequestManager;

import com.google.inject.servlet.RequestScoped;

@RequestScoped
class RequestScopedAuthenticationAttemptProvider implements Provider<AuthenticationAttempt>{
	
	private final Provider<AuthenticationAttemptRequestManager> authenticationAttemptRequestManager;	
	private final Provider<HttpServletRequest> requestProvider;
	
	@Inject
	RequestScopedAuthenticationAttemptProvider(Provider<HttpServletRequest> requestProvider,
											   Provider<AuthenticationAttemptRequestManager> authenticationAttemptRequestManager){
		this.requestProvider = requestProvider;
		this.authenticationAttemptRequestManager = authenticationAttemptRequestManager;
	}
	
	@Override
	public AuthenticationAttempt get() {
		return authenticationAttemptRequestManager.get().retrieveFromRequest(requestProvider.get());
	}
}
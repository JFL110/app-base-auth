package org.jfl110.impl.auth;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;

import org.jfl110.api.auth.AuthTokenStatus;
import org.jfl110.api.auth.AuthenticationAttempt;
import org.jfl110.api.auth.AuthenticationAttemptRequestManager;
import org.jfl110.api.auth.InvalidAuthException;
import org.jfl110.api.auth.MandatoryAuthRequired;
import org.jfl110.spi.auth.RequestAuthenticationService;

@javax.ws.rs.ext.Provider
@MandatoryAuthRequired
public class MandatoryAuthFilter implements ContainerRequestFilter{
	
	private final Provider<RequestAuthenticationService> requestAuthenticationService;
	private final Provider<AuthenticationAttemptRequestManager> authenticationAttemptRequestManager;
	private final Provider<HttpServletRequest> requestProvider;
	
	@Inject
	MandatoryAuthFilter(Provider<RequestAuthenticationService> requestAuthenticationService,
			 Provider<AuthenticationAttemptRequestManager> authenticationAttemptRequestManager,
			 Provider<HttpServletRequest> requestProvider){
		this.requestAuthenticationService = requestAuthenticationService;
		this.authenticationAttemptRequestManager = authenticationAttemptRequestManager;
		this.requestProvider = requestProvider;
	}
	
	
	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		
		AuthenticationAttempt authAttempt = requestAuthenticationService.get().authenticateRequest(requestContext);
		
		if(authAttempt.authToken().isPresent() && authAttempt.status() ==AuthTokenStatus.VALID){
			authenticationAttemptRequestManager.get().insertIntoRequest(requestProvider.get(), authAttempt);
			return;
		}
		
		if(!authAttempt.authToken().isPresent() && authAttempt.status() == AuthTokenStatus.VALID){
			throw new IllegalStateException("Token was not populated but status was declared as valid");
		}
		
		throw new InvalidAuthException(authAttempt.status());
	}
}

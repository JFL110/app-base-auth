package org.jfl110.impl.auth;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jfl110.api.auth.AuthTokenStatus;
import org.jfl110.api.auth.AuthenticationAttempt;
import org.jfl110.api.auth.AuthenticationAttemptRequestManager;
import org.jfl110.api.auth.InvalidAuthException;
import org.jfl110.api.auth.MandatoryAuthFilter;
import org.jfl110.spi.auth.RequestAuthenticationService;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class MandatoryAuthFilterImpl implements MandatoryAuthFilter {

	private final Provider<RequestAuthenticationService> requestAuthenticationService;
	private final Provider<AuthenticationAttemptRequestManager> authenticationAttemptRequestManager;
	
	@Inject
	MandatoryAuthFilterImpl(Provider<RequestAuthenticationService> requestAuthenticationService,
			 Provider<AuthenticationAttemptRequestManager> authenticationAttemptRequestManager){
		this.requestAuthenticationService = requestAuthenticationService;
		this.authenticationAttemptRequestManager = authenticationAttemptRequestManager;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		AuthenticationAttempt authAttempt = requestAuthenticationService.get().authenticateRequest(request);
		
		if(authAttempt.authToken().isPresent() && authAttempt.status() ==AuthTokenStatus.VALID){
			authenticationAttemptRequestManager.get().insertIntoRequest(request, authAttempt);
			chain.doFilter(request, response);
			return;
		}
		
		if(!authAttempt.authToken().isPresent() && authAttempt.status() == AuthTokenStatus.VALID){
			throw new IllegalStateException("Token was not populated but status was declared as valid");
		}
		
		if(authAttempt.status() ==AuthTokenStatus.NOT_FOUND){
			throw new InvalidAuthException("NO_TOKEN","User is not authorised - no token found.");
		}
		
		if(authAttempt.status() ==AuthTokenStatus.EXPIRED){
			throw new InvalidAuthException("TOKEN_EXPIRED","User is not authorised - the token has expied.");
		}
		
		throw new InvalidAuthException("INVALID_TOKEN","User is not authorised.");
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

	
	@Override
	public void destroy() {}
}
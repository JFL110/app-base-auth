package org.jfl110.impl.auth;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.container.ContainerRequestContext;

import org.jfl110.api.auth.AuthTokenClaim;
import org.jfl110.api.auth.AuthTokenStatus;
import org.jfl110.api.auth.AuthenticationAttempt;
import org.jfl110.api.auth.AuthenticationAttemptRequestManager;
import org.jfl110.api.auth.InvalidAuthException;
import org.jfl110.spi.auth.AuthenticationAttemptImpl;
import org.jfl110.spi.auth.RequestAuthenticationService;
import org.junit.Test;

import com.google.common.base.Optional;
import com.google.inject.util.Providers;

/**
 * Tests auth filtering
 *
 * @author JFL110
 */
public class TestMandatoryAuthFilter {

	private final AuthTokenClaim claim = mock(AuthTokenClaim.class);
	private final ContainerRequestContext requestContext = mock(ContainerRequestContext.class);
	private final HttpServletRequest request = mock(HttpServletRequest.class);
	private final RequestAuthenticationService authService = mock(RequestAuthenticationService.class);
	private final AuthenticationAttemptRequestManager attemptManager = new DefaultAuthenticationAttemptRequestManager();
	private final MandatoryAuthFilter filter = new MandatoryAuthFilter(Providers.of(authService),Providers.of(attemptManager),Providers.of(request));


	@Test
	public void testValid() throws Exception{
		
		AuthenticationAttempt attempt = new AuthenticationAttemptImpl(Optional.of(claim),AuthTokenStatus.VALID);
		when(authService.authenticateRequest(requestContext)).thenReturn(attempt);
		
		filter.filter(requestContext);
		
		verify(request).setAttribute("auth-attempt", attempt);
	}
	
	
	
	@Test(expected =InvalidAuthException.class)
	public void testInvalidToken() throws Exception{
		
		AuthenticationAttempt attempt = new AuthenticationAttemptImpl(Optional.of(claim),AuthTokenStatus.OTHER_NOT_VALID);
		when(authService.authenticateRequest(requestContext)).thenReturn(attempt);
		
		filter.filter(requestContext);
	}
	
	
	@Test(expected =IllegalStateException.class)
	public void testBadAuthResponse() throws Exception{
		
		AuthenticationAttempt attempt = new AuthenticationAttemptImpl(Optional.<AuthTokenClaim>absent(),AuthTokenStatus.VALID);
		when(authService.authenticateRequest(requestContext)).thenReturn(attempt);
		
		filter.filter(requestContext);
	}
}
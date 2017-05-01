package org.jfl110.spi.auth.rolling.utils;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jfl110.api.auth.AuthTokenClaim;
import org.jfl110.api.auth.HashedAuthToken;
import org.jfl110.spi.auth.utils.RandomKeyGeneratorService;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.google.inject.util.Providers;

/**
 * Tests Token Issuing
 *
 *
 * @author JFL110
 */
public class TestTokenIssuing {

	private final RandomKeyGeneratorService randomKeyGeneratorService = mock(RandomKeyGeneratorService.class);
	private final AuthTokenRollingKeyHashService authTokenHashService = mock(AuthTokenRollingKeyHashService.class);
	private final LocalDateTime issueTime = new LocalDateTime(2010,1,1,1,1);

	private final HashedTokenIssuer tokenIssuer = new HashedTokenIssuer.Impl(Providers.of(randomKeyGeneratorService), Providers.of(issueTime), Providers.of(authTokenHashService));


	@Test
	public void testTokenGeneration(){
		
		when(randomKeyGeneratorService.newHexKey(Mockito.anyInt())).thenReturn("NONCE");
		when(authTokenHashService.hashClaim(Mockito.any(AuthTokenClaim.class))).thenReturn("HASH");
		
		HashedAuthToken token = tokenIssuer.issue()
								.nonceLength(32)
								.userId("JOHN")
								.claims(Lists.newArrayList("CLAIM1"))
								.build();
		
		assertEquals("JOHN",token.userIdentifier());
		assertEquals(issueTime,token.issuedTime());
		assertThat(token.claims(),hasSize(1));
		assertEquals("CLAIM1",token.claims().get(0));
		assertEquals("HASH",token.hash());
		assertEquals("NONCE",token.nonce());
	}
	
	
	@Test
	public void testDefaultKeyLength(){
		
		tokenIssuer.issue()
		.userId("JOHN")
		.build();
		
		verify(randomKeyGeneratorService).newHexKey(16);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testMustSpecifyUserName(){
		tokenIssuer.issue().build();
	}
}
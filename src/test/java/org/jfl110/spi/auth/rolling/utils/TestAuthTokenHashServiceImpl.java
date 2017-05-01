package org.jfl110.spi.auth.rolling.utils;

import static com.google.inject.util.Providers.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.nio.charset.Charset;

import org.apache.commons.codec.binary.Hex;
import org.jfl110.api.auth.AuthTokenClaim;
import org.jfl110.api.auth.AuthTokenStatus;
import org.jfl110.api.auth.HashedAuthToken;
import org.jfl110.api.auth.rolling.KeyStoreLookupResult;
import org.jfl110.api.auth.rolling.RollingKey;
import org.jfl110.api.auth.rolling.RollingKeyStore;
import org.jfl110.api.auth.rolling.RollingKeyStore.Reader.KeyLookupResultTuple;
import org.jfl110.spi.auth.utils.HashingService;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;

/**
 * Tests AuthTokenRollingKeyHashService
 *
 * @author JFL110
 */
public class TestAuthTokenHashServiceImpl{
	
	public TestAuthTokenHashServiceImpl() throws Exception{}

	private final RollingKeyStore.Reader keyStoreReader = mock(RollingKeyStore.Reader .class);
	private final HashingService hashingService = mock(HashingService.class);
	private final AuthTokenRollingKeyHashService service = new AuthTokenRollingKeyHashService
								.AuthTokenRollingKeyHashServiceImpl(of(keyStoreReader), of(hashingService));
	
	private final Charset charset = Charset.forName("UTF-8");
	
	private final byte[] hashedBytes = "HashedBytes".getBytes(charset);
	private final String hashParts = "noneuserId2010-01-01T01:01:00.000claim1|claim2KeyBytes";
	
	private final LocalDateTime issueTime = new LocalDateTime(2010,1,1,1,1);
	private final KeyLookupResultTuple absentKeyTuple = absentKeyTuple(KeyStoreLookupResult.EXPIRED);
	private final KeyLookupResultTuple presentKeyTuple = presentKeyTuple("KeyBytes".getBytes(charset));
	private final ArgumentCaptor<byte[]> byteCaptor = ArgumentCaptor.forClass(byte[].class);
	
	
	@Test(expected=IllegalStateException.class)
	public void testNoKeyForTime(){
		when(keyStoreReader.getRollingKey(issueTime)).thenReturn(absentKeyTuple);
		service.hashClaim(claim());
	}
	
	
	@Test
	public void testHashing(){
		when(keyStoreReader.getRollingKey(issueTime)).thenReturn(presentKeyTuple);
		when(hashingService.hash(Mockito.any(byte[].class))).thenReturn(hashedBytes);
		
		String hashedClaim = service.hashClaim(claim());
		
		verify(hashingService,times(1)).hash(byteCaptor.capture());
		assertEquals(hashParts,new String(byteCaptor.getValue(),charset));
		assertEquals(new String(Hex.encodeHex(hashedBytes)),hashedClaim);
	}
	
	
	@Test
	public void testHashMatch(){
		when(keyStoreReader.getRollingKey(issueTime)).thenReturn(presentKeyTuple);
		when(hashingService.hash(Mockito.any(byte[].class))).thenReturn(hashedBytes);
		
		AuthTokenStatus tokenStatus = service.validate(hashedClaim(new String(Hex.encodeHex(hashedBytes))));
		
		verify(hashingService,times(1)).hash(byteCaptor.capture());
		assertEquals(hashParts,new String(byteCaptor.getValue(),charset));
		assertEquals(AuthTokenStatus.VALID,tokenStatus);
	}
	
	
	@Test
	public void testExpiredToken(){
		when(keyStoreReader.getRollingKey(issueTime)).thenReturn(absentKeyTuple);
		AuthTokenStatus tokenStatus = service.validate(hashedClaim(new String(Hex.encodeHex(hashedBytes))));
		assertEquals(AuthTokenStatus.EXPIRED,tokenStatus);
	}
	
	
	private AuthTokenClaim claim(){
		AuthTokenClaim claim = mock(AuthTokenClaim.class);
		when(claim.issuedTime()).thenReturn(issueTime);
		when(claim.userIdentifier()).thenReturn("userId");
		when(claim.nonce()).thenReturn("none");
		when(claim.claims()).thenReturn(Lists.newArrayList("claim1","claim2"));
		return claim;
	}
	
	
	private HashedAuthToken hashedClaim(String hash){
		HashedAuthToken claim = mock(HashedAuthToken.class);
		when(claim.issuedTime()).thenReturn(issueTime);
		when(claim.userIdentifier()).thenReturn("userId");
		when(claim.nonce()).thenReturn("none");
		when(claim.claims()).thenReturn(Lists.newArrayList("claim1","claim2"));
		when(claim.hash()).thenReturn(hash);
		return claim;
	}
	
	
	private KeyLookupResultTuple absentKeyTuple(KeyStoreLookupResult lookupResult){
		KeyLookupResultTuple tuple = mock(KeyLookupResultTuple.class);
		when(tuple.key()).thenReturn(Optional.<RollingKey>absent());
		when(tuple.lookupResult()).thenReturn(lookupResult);
		return tuple;
	}
	
	
	private KeyLookupResultTuple presentKeyTuple(byte[] keyBytes){
		RollingKey key = mock(RollingKey.class);
		when(key.key()).thenReturn(keyBytes);
		
		KeyLookupResultTuple tuple = mock(KeyLookupResultTuple.class);
		when(tuple.key()).thenReturn(Optional.of(key));
		when(tuple.lookupResult()).thenReturn(KeyStoreLookupResult.FOUND_KEY);
		return tuple;
		
	}
}
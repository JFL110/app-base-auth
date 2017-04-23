package org.jfl110.impl.auth.rolling;

import static com.google.inject.util.Providers.of;
import static org.jfl110.impl.auth.rolling.RollingKeyTestingUtils.CURRENT_TIME;
import static org.jfl110.impl.auth.rolling.RollingKeyTestingUtils.key;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.jfl110.api.auth.rolling.KeyStoreLookupResult;
import org.jfl110.api.auth.rolling.RollingKey;
import org.jfl110.api.auth.rolling.RollingKeyStore;
import org.jfl110.api.auth.rolling.RollingKeyStore.Reader.KeyLookupResultTuple;
import org.jfl110.impl.auth.rolling.DefaultRollingKeyStore;
import org.jfl110.spi.auth.rolling.RollingKeyService;
import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * Tests DefaultRollingKeyStore
 *
 * @author JFL110
 */
public class TestDefaultRollingKeyStore {



	private final RollingKeyService rollingKeyService = mock(RollingKeyService.class);
	private final RollingKeyStore.Reader keyStore = new DefaultRollingKeyStore(of(rollingKeyService), of(CURRENT_TIME));

	private final List<RollingKey> validKeySet = Lists.newArrayList(
			key(-5), key(-4), key(-3), key(-2), key(-1), key(0),
			key(1), key(2), key(3), key(4),key(5));
	
	private final List<RollingKey> keySetWithGap = Lists.newArrayList(
			key(-5), key(-4), key(-3), key(-2), key(-1), key(0),
			key(1), key(2),         key(4),key(5));
	
	private final List<RollingKey> keySetWithOverlap = Lists.newArrayList(
			key(-5), key(-4), key(-3), key(-2), key(-1), key(0),
			key(1), key(2),key(3), key(4), key(4),key(5));
	
	
	@Test(expected = IllegalStateException.class)
	public void testKeySetWithGap() {
		when(rollingKeyService.getAllKeys()).thenReturn(keySetWithGap);
		keyStore.getRollingKey(CURRENT_TIME);
	}
	
	
	@Test(expected = IllegalStateException.class)
	public void testKeySetWithOverlap() {
		when(rollingKeyService.getAllKeys()).thenReturn(keySetWithOverlap);
		keyStore.getRollingKey(CURRENT_TIME);
	}
	
	
	@Test
	public void testFuture() {
		when(rollingKeyService.getAllKeys()).thenReturn(validKeySet);

		KeyLookupResultTuple result = keyStore.getRollingKey(CURRENT_TIME.plusSeconds(6));
		
		assertEquals(KeyStoreLookupResult.TIME_IN_FUTURE,result.lookupResult());
		assertFalse(result.key().isPresent());
	}
	
	
	@Test
	public void testExpiredKey() {
		when(rollingKeyService.getAllKeys()).thenReturn(validKeySet);

		KeyLookupResultTuple result = keyStore.getRollingKey(CURRENT_TIME.minusSeconds(6 * 60));
		
		assertEquals(KeyStoreLookupResult.EXPIRED,result.lookupResult());
		assertFalse(result.key().isPresent());
	}
	
	
	
	@Test
	public void testCacheRefreshOnce() {
		when(rollingKeyService.getAllKeys()).thenReturn(validKeySet);

		keyStore.getRollingKey(CURRENT_TIME.minusSeconds(4 * 60));
		keyStore.getRollingKey(CURRENT_TIME.minusSeconds(4 * 60));
		
		verify(rollingKeyService,times(1)).getAllKeys();
	}
	
	
	@Test
	public void testKeyInValidPastInterval() {
		when(rollingKeyService.getAllKeys()).thenReturn(validKeySet);

		KeyLookupResultTuple result = keyStore.getRollingKey(CURRENT_TIME.minusSeconds(4 * 60));
		
		assertEquals(KeyStoreLookupResult.FOUND_KEY,result.lookupResult());
		assertTrue(result.key().isPresent());
		assertEquals(-4,result.key().get().keyNumber());
	}
	
	

	@Test
	public void testKeyInValidCurrentInterval() {
		when(rollingKeyService.getAllKeys()).thenReturn(validKeySet);

		KeyLookupResultTuple result = keyStore.getRollingKey(CURRENT_TIME.minusSeconds(5));
		
		assertEquals(KeyStoreLookupResult.FOUND_KEY,result.lookupResult());
		assertTrue(result.key().isPresent());
		assertEquals(-1,result.key().get().keyNumber());
	}
	
	
	@Test
	public void testKeyInTolleratedFutureInterval() {
		when(rollingKeyService.getAllKeys()).thenReturn(validKeySet);

		KeyLookupResultTuple result = keyStore.getRollingKey(CURRENT_TIME.plusSeconds(4));
		
		assertEquals(KeyStoreLookupResult.FOUND_KEY,result.lookupResult());
		assertTrue(result.key().isPresent());
		assertEquals(0,result.key().get().keyNumber());
	}
	


}
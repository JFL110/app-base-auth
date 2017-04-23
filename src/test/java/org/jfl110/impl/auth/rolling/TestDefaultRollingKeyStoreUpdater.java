package org.jfl110.impl.auth.rolling;

import static com.google.inject.util.Providers.of;
import static org.jfl110.impl.auth.rolling.RollingKeyTestingUtils.CURRENT_TIME;
import static org.jfl110.impl.auth.rolling.RollingKeyTestingUtils.key;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.jfl110.api.auth.rolling.RollingKey;
import org.jfl110.api.auth.rolling.RollingKeyStore;
import org.jfl110.impl.auth.rolling.DefaultRollingKeyStoreUpdater;
import org.jfl110.spi.auth.rolling.RollingKeyService;
import org.jfl110.spi.auth.utils.RandomKeyGeneratorService;
import org.joda.time.LocalDateTime;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Lists;

/**
 * Tests DefaultRollingKeyStoreUpdater
 *
 * @author JFL110
 */
public class TestDefaultRollingKeyStoreUpdater {

	private final RollingKeyService rollingKeyService = mock(RollingKeyService.class);
	private final RandomKeyGeneratorService randomKeyGeneratorService = mock(RandomKeyGeneratorService.class);
	private final RollingKeyStore.Updater keyStore = new DefaultRollingKeyStoreUpdater(of(rollingKeyService), of(randomKeyGeneratorService));
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private final ArgumentCaptor<List<RollingKey>> keyListCaptor = ArgumentCaptor.forClass((Class<List<RollingKey>>) (Class)List.class);
	
	private final List<RollingKey> currentKeys = Lists.newArrayList(
			key(-5), key(-4), key(-3), key(-2), key(-1), key(0),
			key(1), key(2), key(3), key(4),key(5));
	
	@Test
	public void test(){
		when(rollingKeyService.getAllKeys()).thenReturn(currentKeys);
		final LocalDateTime addUntil = CURRENT_TIME.plusMinutes(10);
		final LocalDateTime deleteBefore = CURRENT_TIME.minusMinutes(4);
		
		keyStore.updateUntil(deleteBefore,addUntil);
		
		verify(rollingKeyService,times(1)).replaceAllKeys(keyListCaptor.capture());
		assertEquals(15,keyListCaptor.getValue().size());
		// TODO
	}
}
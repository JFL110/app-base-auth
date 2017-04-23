package org.jfl110.impl.auth.rolling;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jfl110.api.auth.rolling.RollingKey;
import org.joda.time.LocalDateTime;

class RollingKeyTestingUtils {

	static final int KEY_LENGTH_SECONDS = 60;
	static final LocalDateTime CURRENT_TIME = new LocalDateTime(2012, 1, 1, 1, 1, 1);
	
	static RollingKey key(int offset) {
		RollingKey key = mock(RollingKey.class);
		when(key.keyNumber()).thenReturn((long) offset);
		when(key.validFrom()).thenReturn(CURRENT_TIME.plusSeconds(offset * KEY_LENGTH_SECONDS));
		when(key.validForSeconds()).thenReturn(KEY_LENGTH_SECONDS);
		return key;
	}
}

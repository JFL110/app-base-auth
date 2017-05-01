package org.jfl110.impl.auth.rolling;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jfl110.api.auth.rolling.RollingKey;
import org.jfl110.api.auth.rolling.RollingKeyStore;
import org.jfl110.spi.auth.rolling.RollingKeyService;
import org.jfl110.spi.auth.utils.RandomKeyGeneratorService;
import org.joda.time.LocalDateTime;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class DefaultRollingKeyStoreUpdater implements RollingKeyStore.Updater{
	
	// TODO config
	private static final int MAX_EXTRA_KEYS = 100;
	private static final int SECONDS_PER_KEY = 60;
	private static final int KEY_LENGTH = 32;
	private static final long FIRST_KEY_NUMBER = 1;
	
	private final Provider<RollingKeyService> rollingKeyService;
	private final Provider<RandomKeyGeneratorService> randomKeyGeneratorService;
	
	private static final Comparator<RollingKey> SORT_BY_VALID_FROM = new Comparator<RollingKey>(){
		@Override
		public int compare(RollingKey o1, RollingKey o2) {
			return o1.validFrom().compareTo(o2.validFrom());
		}
	};
	
	@Inject
	DefaultRollingKeyStoreUpdater(Provider<RollingKeyService> rollingKeyService,
			Provider<RandomKeyGeneratorService> randomKeyGeneratorService){
		this.rollingKeyService = rollingKeyService;
		this.randomKeyGeneratorService = randomKeyGeneratorService;
	}

	
	@Override
	public void updateUntil(LocalDateTime deleteBefore,LocalDateTime addUntil) {
		Iterable<RollingKey> newKeys = keysUntil(rollingKeyService.get().getAllKeys(),deleteBefore,addUntil);
		// TODO check new keys
		rollingKeyService.get().replaceAllKeys(newKeys);
	}
	
	
	private Iterable<RollingKey> keysUntil(Iterable<RollingKey> existingKeys,LocalDateTime deleteBefore,LocalDateTime addUntil){
		
		List<RollingKey> newKeys = Lists.newArrayList(FluentIterable
										.from(existingKeys)
										.filter(excludeKeysOutOfRange(deleteBefore,addUntil))
										.toSortedList(SORT_BY_VALID_FROM));
		
		RollingKey lastKey = newKeys.size() == 0 ? null : newKeys.get(newKeys.size() - 1);
		for(int i = 0; i < MAX_EXTRA_KEYS; i++){
			
			LocalDateTime validUntil = lastKey == null ? deleteBefore : lastKey.validFrom().plusSeconds(lastKey.validForSeconds());
			
			if(lastKey != null && validUntil.isAfter(addUntil)){
				return newKeys;
			}
			
			lastKey = new NewKey(lastKey == null ? FIRST_KEY_NUMBER : lastKey.keyNumber()+1,
							 	 randomKeyGeneratorService.get().newByteKey(KEY_LENGTH),
								 validUntil,
								 SECONDS_PER_KEY);
			
			newKeys.add(lastKey);
		}
		
		throw new IllegalStateException("Number of new keys is over the limit ["+MAX_EXTRA_KEYS+"]");
	}
	
	
	
	private Predicate<RollingKey> excludeKeysOutOfRange(final LocalDateTime pastLimit,final LocalDateTime futureLimit){
		return new Predicate<RollingKey>(){
			@Override
			public boolean apply(RollingKey input) {
				return input.validFrom().plusSeconds(input.validForSeconds()).isAfter(pastLimit) &&
					   input.validFrom().isBefore(futureLimit);
			}
		};
	}
	
	
	private static class NewKey implements RollingKey{

		private final long keyNumber;
		private final byte[] key;
		private final LocalDateTime validFrom;
		private final int validForSeconds;
		
		private NewKey(long keyNumber,byte[] key,LocalDateTime validFrom,int validForSeconds){
			this.keyNumber = keyNumber;
			this.key = key;
			this.validFrom = validFrom;
			this.validForSeconds = validForSeconds;
		}
		
		@Override
		public long keyNumber() {
			return keyNumber;
		}

		@Override
		public byte[] key() {
			return key;
		}

		@Override
		public LocalDateTime validFrom() {
			return validFrom;
		}

		@Override
		public int validForSeconds() {
			return validForSeconds;
		}
	}
}
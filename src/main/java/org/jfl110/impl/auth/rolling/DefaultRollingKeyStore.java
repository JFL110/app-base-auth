package org.jfl110.impl.auth.rolling;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import org.jfl110.api.auth.rolling.KeyStoreLookupResult;
import org.jfl110.api.auth.rolling.RollingKey;
import org.jfl110.api.auth.rolling.RollingKeyStore;
import org.jfl110.api.time.CurrentTime;
import org.jfl110.spi.auth.rolling.RollingKeyService;
import org.joda.time.LocalDateTime;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class DefaultRollingKeyStore implements RollingKeyStore.Reader{
	
	private static Logger logger = Logger.getLogger(DefaultRollingKeyStore.class.getSimpleName());
	
	// TODO config
	private static final int MANDATORY_FORWARD_SECONDS = 60 * 5;
	private static final int FUTURE_TOLLERANCE_SECONDS = 5;
	
	private static final Comparator<RollingKey> SORT_BY_VALID_FROM = new Comparator<RollingKey>(){
		@Override
		public int compare(RollingKey o1, RollingKey o2) {
			return o1.validFrom().compareTo(o2.validFrom());
		}
	};
	
	private KeyCache keyCache;
	
	private final Provider<RollingKeyService> rollingKeyService;
	private final Provider<LocalDateTime> currentTimeProvider;
	
	@Inject
	DefaultRollingKeyStore(Provider<RollingKeyService> rollingKeyService,
			@CurrentTime Provider<LocalDateTime> currentTimeProvider){
		this.rollingKeyService = rollingKeyService;
		this.currentTimeProvider = currentTimeProvider;
	}

	
	@Override
	public KeyLookupResultTuple getRollingKey(LocalDateTime validTime) {
		
		if(validTime.isAfter(currentTimeProvider.get().plusSeconds(FUTURE_TOLLERANCE_SECONDS))){
			return new KeyLookupResultTuple(Optional.<RollingKey>absent(),KeyStoreLookupResult.TIME_IN_FUTURE);
		}
		
		KeyCache keyCache = refreshCacheIfRequired();

		if(validTime.isBefore(keyCache.earliestValidTime)){
			return new KeyLookupResultTuple(Optional.<RollingKey>absent(),KeyStoreLookupResult.EXPIRED);
		}
		
		for(RollingKey key : keyCache.sortedKeys){
			if(validTime.isAfter(key.validFrom()) && validTime.isBefore(validEndTime(key))){
				return new KeyLookupResultTuple(Optional.of(key),KeyStoreLookupResult.FOUND_KEY);
			}
		}
		
		for(RollingKey key : keyCache.sortedKeys){
			if(validTime.isEqual(key.validFrom())){
				return new KeyLookupResultTuple(Optional.of(key),KeyStoreLookupResult.FOUND_KEY);
			}
		}
		
		throw new IllegalStateException("Time was between cache times but no key interval was found.");
	}
	
	
	private KeyCache refreshCacheIfRequired(){
		if(cacheIsValid(false)){
			return keyCache;
		}
		
		return refreshCache();
	}
	
	
	private synchronized KeyCache refreshCache(){
		List<RollingKey> sortedKeys = FluentIterable.from(rollingKeyService.get().getAllKeys())
											  		.toSortedList(SORT_BY_VALID_FROM);
		
		if(sortedKeys.isEmpty()){
			throw new IllegalStateException("Failed to refresh key cache - no keys.");
		}
		
		checkNoOverlapsOrGaps(sortedKeys);
		
		LocalDateTime earliestValidTime = sortedKeys.get(0).validFrom();
		LocalDateTime validUntil = validEndTime(sortedKeys.get(sortedKeys.size()-1));
		
		keyCache = new KeyCache(sortedKeys, earliestValidTime, validUntil);
		
		if(!cacheIsValid(true)){
			throw new IllegalStateException("Failed to refresh key cache.");
		}
		
		return keyCache;
	}
	
	
	private void checkNoOverlapsOrGaps(List<RollingKey> sortedKeys){
		LocalDateTime endTimeCounter = validEndTime(sortedKeys.get(0));
		
		Optional<String> errorMsg = Optional.absent();
		
		for(int i = 1; i < sortedKeys.size(); i++){
			
			if(!endTimeCounter.isEqual(sortedKeys.get(i).validFrom())){
				if(endTimeCounter.isBefore(sortedKeys.get(i).validFrom())){
					errorMsg = Optional.of("Gap between keys ["+sortedKeys.get(i-1).keyNumber()+"] and ["+sortedKeys.get(i).keyNumber()+"]");
				}else{
					errorMsg = Optional.of("Overlap on keys ["+sortedKeys.get(i-1).keyNumber()+"] and ["+sortedKeys.get(i).keyNumber()+"]");
				}
				break;
			}
			
			endTimeCounter = endTimeCounter.plusSeconds(sortedKeys.get(i).validForSeconds());
		}
		
		if(errorMsg.isPresent()){
			logKeys(sortedKeys);
			throw new IllegalStateException(errorMsg.get());
		}
	}
	
	
	private void logKeys(List<RollingKey> sortedKeys){
		for(int i = 0; i < sortedKeys.size(); i++){
			RollingKey key = sortedKeys.get(i);
			System.out.println("Key["+i+"] "+key.keyNumber()+" "+key.validFrom()+" "+key.validForSeconds());
		}
	}
	
	
	private LocalDateTime validEndTime(RollingKey key){
		return key.validFrom().plusSeconds(key.validForSeconds());
	}
	
	
	private boolean cacheIsValid(boolean log){
		KeyCache currentKeyCache = keyCache;
		
		if(currentKeyCache == null){
			if(log){
				logger.severe("No key cache");
			}
			return false;
		}
		
		if(!currentKeyCache.earliestValidTime.isBefore(currentTimeProvider.get())){
			if(log){
				logger.severe("Key cache invalid, earliest valid time is in the future: "+currentKeyCache.earliestValidTime +" < "+currentTimeProvider.get());
			}
			return false;
		}
		
		if(!currentKeyCache.validUntil.isAfter(currentTimeProvider.get().plusSeconds(MANDATORY_FORWARD_SECONDS))){
			if(log){
				logger.severe("Key cache invalid, valid until is not far enough in the future: "+currentKeyCache.validUntil +" - "+currentTimeProvider.get() + " > "+MANDATORY_FORWARD_SECONDS);
			}
			return false;
		}
		
		return true;
	}
	
	
	private static final class KeyCache{
		private final List<RollingKey> sortedKeys;
		private final LocalDateTime earliestValidTime;
		private final LocalDateTime validUntil;
		
		private KeyCache(List<RollingKey> keys,LocalDateTime earliestValidTime,LocalDateTime validUntil){
			this.sortedKeys = keys;
			this.earliestValidTime = earliestValidTime;
			this.validUntil = validUntil;
		}
	}
}

package org.jfl110.api.auth.rolling;

import org.joda.time.LocalDateTime;

import com.google.common.base.Optional;

/**
 * Read and Write interfaces for the RollingKeyStore.
 * 
 * The RollingKeyStore contains a number of RollingKeys which are valid for 
 * a specified period time. Together the RollingKeys form an unbroken and
 * un-overlapping chain which covers some time in the past and some time in the
 * future.
 * 
 * Periodic calls to Updater.updateUntil keep the RollingKeyStore up to date. If the
 * store is not kept up to date it will not be possible to issue tokens and exceptions will
 * be thrown.
 *
 * @author JFL110
 */
public interface RollingKeyStore {

	/**
	 * Fetches the key that covers a specific time from the key store.
	 *
	 * @author JFL110
	 */
	public interface Reader{
		
		
		/**
		 * Tuple of a retrieved RollingKey or the reason for failure
		 *
		 * @author JFL110
		 */
		public static class KeyLookupResultTuple{
			
			private final Optional<RollingKey> key;
			private final KeyStoreLookupResult lookupResult;
			
			public KeyLookupResultTuple(Optional<RollingKey> key,KeyStoreLookupResult lookupResult){
				this.key = key;
				this.lookupResult = lookupResult;
			}
			
			
			public Optional<RollingKey> key(){
				return key;
			}
			
			
			public KeyStoreLookupResult lookupResult(){
				return lookupResult;
			}
		}
		
		
		/**
		 * Fetches the RollingKey that is valid for the specified time.
		 * 
		 * @param validTime the time the key must cover
		 * @return a tuple of the key or failure reason
		 */
		KeyLookupResultTuple getRollingKey(LocalDateTime validTime);
	}
	
	
	/**
	 * Updates the data that backs the key store.
	 *
	 * @author JFL110
	 */
	public interface Updater {
		
		
		/**
		 * Adds and removes keys to cover the specified range of times.
		 * 
		 * @param deleteBefore the limit to expire keys before
		 * @param addUntil the limit to add keys until
		 */
		void updateUntil(LocalDateTime deleteBefore,LocalDateTime addUntil);
	}
}
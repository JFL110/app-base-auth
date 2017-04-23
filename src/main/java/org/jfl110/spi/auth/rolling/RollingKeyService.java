package org.jfl110.spi.auth.rolling;

import org.jfl110.api.auth.rolling.RollingKey;

/**
 * Persistence service for RollingKeys.
 * 
 * Applications using this module will need to provide an implementation for this service.
 *
 * @author JFL110
 */
public interface RollingKeyService {

	/**
	 * Deletes all stored keys and replaces them with the input keys
	 */
	void replaceAllKeys(Iterable<RollingKey> keys);
	
	
	/**
	 * Gets all stored keys.
	 */
	Iterable<RollingKey> getAllKeys();
}
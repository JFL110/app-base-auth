package org.jfl110.api.auth.rolling;

/**
 * Encapsulates the reason for failing to retrieve a key or success.
 *
 * @author JFL110
 */
public enum KeyStoreLookupResult {
	/**
	 * Indicates success
	 */
	FOUND_KEY,
	
	/**
	 * The lookup time was after the current time and so no key could be retrieved
	 */
	TIME_IN_FUTURE,
	
	/**
	 * The lookup time was too long ago for the key store to provide a key
	 */
	EXPIRED
}
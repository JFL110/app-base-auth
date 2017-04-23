package org.jfl110.api.auth.rolling;

import org.joda.time.LocalDateTime;

/**
 * A hashing key which is valid for a certain period of time
 *
 * @author JFL110
 */
public interface RollingKey {
	
	/**
	 * @return the key reference number
	 */
	long keyNumber();

	
	/**
	 * @return the key
	 */
	byte[] key();
	
	
	/**
	 * @return the time that the key is valid from
	 */
	LocalDateTime validFrom();
	
	
	/**
	 * @return how long the key is valid for, in seconds
	 */
	int validForSeconds();
}
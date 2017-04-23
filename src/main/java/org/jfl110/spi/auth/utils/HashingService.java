package org.jfl110.spi.auth.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.inject.ImplementedBy;

/**
 * Produces hashes for generic messages.
 *
 * @author JFL110
 */
@ImplementedBy(HashingService.HashingServiceImpl.class)
public interface HashingService {
	
	/**
	 * Hash a message
	 * 
	 * @param message the message to hash
	 * @return the hashed message
	 */
	byte[] hash(byte[] message);

	
	/**
	 * Default implementation of HashingService
	 *
	 * @author JFL110
	 */
	static class HashingServiceImpl implements HashingService{
		
		private static final String ALGORITHM = "SHA-512";

		@Override
		public byte[] hash(byte[] message) {
			try {
				return MessageDigest.getInstance(ALGORITHM).digest(message);
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
		}
	}
}

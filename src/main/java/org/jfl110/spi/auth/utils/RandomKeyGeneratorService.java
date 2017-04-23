package org.jfl110.spi.auth.utils;

import java.security.SecureRandom;
import java.util.Random;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.ImplementedBy;

/**
 * Generates secure random keys.
 *
 * @author JFL110
 */
@ImplementedBy(RandomKeyGeneratorService.RandomKeyGeneratorServiceImpl.class)
public interface RandomKeyGeneratorService {

	/**
	 * Generates a new random byte key of a specified length.
	 */
	byte[] newByteKey(int length);
	
	/**
	 * Generates a new string key derived from a specified length byte key.
	 */
	String newHexKey(int byteLength);
	
	
	/**
	 * Default implementation of RandomKeyGeneratorService.
	 *
	 * @author JFL110
	 */
	static class RandomKeyGeneratorServiceImpl implements RandomKeyGeneratorService{

		private static final Random RANDOM = new SecureRandom();
		
		@Override
		public byte[] newByteKey(int length) {
			byte[] salt = new byte[length];
			RANDOM.nextBytes(salt);
			return salt;
		}

		
		@Override
		public String newHexKey(int byteLength) {
			return new String(Hex.encodeHex(newByteKey(byteLength)));
		}
		
	}
}
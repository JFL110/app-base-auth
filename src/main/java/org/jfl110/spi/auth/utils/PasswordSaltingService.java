package org.jfl110.spi.auth.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.google.inject.ImplementedBy;


/**
 * Salts passwords.
 *
 * @author JFL110
 */
@ImplementedBy(PasswordSaltingService.PasswordSaltingServiceImpl.class)
public interface PasswordSaltingService {

	/**
	 * Get the hash for a salt and password combination. 
	 * This may empty the contents of the supplied password.
	 */
	byte[] hash(char[] password, byte[] salt);

	/**
	 * Checks if a password and salt match the expected hash.
	 */
	boolean hashMatches(char[] password, byte[] salt, byte[] expectedHash);

	
	/**
	 * Default implementation of PasswordSaltingService
	 *
	 * @author JFL110
	 */
	static class PasswordSaltingServiceImpl implements PasswordSaltingService {

		private static final int ITERATIONS = 10000;
		private static final int KEY_LENGTH = 256;

		@Override
		public byte[] hash(char[] password, byte[] salt) {
			PBEKeySpec spec = new PBEKeySpec(password, salt, ITERATIONS, KEY_LENGTH);
			Arrays.fill(password, Character.MIN_VALUE);
			try {
				SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
				return skf.generateSecret(spec).getEncoded();
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				throw new AssertionError("Error while hashing a password: " + e.getMessage(), e);
			} finally {
				spec.clearPassword();
			}
		}

		
		@Override
		public boolean hashMatches(char[] password, byte[] salt,byte[] expectedHash) {
			byte[] pwdHash = hash(password, salt);
		    Arrays.fill(password, Character.MIN_VALUE);
		    
		    if (pwdHash.length != expectedHash.length){
		    	return false;
		    }
		    
		    for (int i = 0; i < pwdHash.length; i++) {
		      if (pwdHash[i] != expectedHash[i]){
		    	  return false;
		      }
		    }
		    return true;
		}
	}
}
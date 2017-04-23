package org.jfl110.spi.auth.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests PasswordSaltingServiceImpl
 *
 * @author JFL110
 */
public class TestPasswordSaltingServiceImpl {

	PasswordSaltingService service = new PasswordSaltingService.PasswordSaltingServiceImpl();
	
	@Test
	public void test(){
		String password = "iAmAPassWord";
		char[] passwordIn = password.toCharArray();
		byte[] salt = new byte[]{0,1,1,1,1,1,0,0,1};
		
		byte[] hash = service.hash(passwordIn, salt);
		assertTrue(service.hashMatches(password.toCharArray(), salt, hash));
		assertFalse(service.hashMatches("anotherPassword".toCharArray(), salt, hash));
	}
}

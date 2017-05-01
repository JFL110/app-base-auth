package org.jfl110.spi.auth.utils;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests HashingService

 * @author JFL110
 */
public class TestHashingServiceImpl {

	private final HashingService hashingService = new HashingService.HashingServiceImpl();
	
	@Test
	public void test() throws Exception{
		byte[] message = "Hello".getBytes("UTF-8");
		byte[] hashedMessage = hashingService.hash(message);
		assertTrue(hashedMessage.length > message.length);
	}
	
}
package org.jfl110.spi.auth.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests RandomKeyGeneratorService
 *
 * @author JFL110
 */
public class TestRandomKeyGeneratorServiceImpl {

	private final RandomKeyGeneratorService service = new RandomKeyGeneratorService.RandomKeyGeneratorServiceImpl();
	
	@Test
	public void testByteKey(){
		byte[] key = service.newByteKey(16);
		assertEquals(16,key.length,16);
	}
	
	
	@Test
	public void testHexKey(){
		String key = service.newHexKey(16);
		assertEquals(32,key.length());
	}
}
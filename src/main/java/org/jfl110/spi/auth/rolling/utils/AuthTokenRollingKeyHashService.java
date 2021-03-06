package org.jfl110.spi.auth.rolling.utils;

import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.codec.binary.Hex;
import org.jfl110.api.auth.AuthTokenClaim;
import org.jfl110.api.auth.AuthTokenStatus;
import org.jfl110.api.auth.HashedAuthToken;
import org.jfl110.api.auth.rolling.KeyStoreLookupResult;
import org.jfl110.api.auth.rolling.RollingKey;
import org.jfl110.api.auth.rolling.RollingKeyStore;
import org.jfl110.api.auth.rolling.RollingKeyStore.Reader.KeyLookupResultTuple;
import org.jfl110.spi.auth.utils.HashingService;

import com.google.common.base.Joiner;
import com.google.common.primitives.Bytes;
import com.google.inject.ImplementedBy;

/**
 * Hashes and validates AuthTokens using RollingKeys. 
 *
 * @author JFL110
 */
@ImplementedBy(AuthTokenRollingKeyHashService.AuthTokenRollingKeyHashServiceImpl.class)
public interface AuthTokenRollingKeyHashService {
	
	/**
	 * Produces a hash for the given claim.
	 */
	String hashClaim(AuthTokenClaim claim);
	
	
	/**
	 * Checks the hash on a claim.
	 */
	AuthTokenStatus validate(HashedAuthToken token);
	
	
	/**
	 * Default implementation of AuthTokenHashService
	 *
	 * @author JFL110
	 */
	static class AuthTokenRollingKeyHashServiceImpl implements AuthTokenRollingKeyHashService{
		
		private static final Charset CHARSET = Charset.forName("UTF-8");
		
		private final Provider<RollingKeyStore.Reader> rollingKeyStoreReader;
		private final Provider<HashingService> hashingService;
		
		@Inject
		AuthTokenRollingKeyHashServiceImpl(Provider<RollingKeyStore.Reader> rollingKeyStoreReader,
				Provider<HashingService> hashingService){
			this.rollingKeyStoreReader = rollingKeyStoreReader;
			this.hashingService = hashingService;
		}

		@Override
		public String hashClaim(AuthTokenClaim claim) {
			
			KeyLookupResultTuple keyLookupResult = rollingKeyStoreReader.get().getRollingKey(claim.issuedTime());
			
			if(!keyLookupResult.key().isPresent()){
				throw new IllegalStateException("No key found for time ["+claim.issuedTime()+"]");
			}
			
			return hashClaim(claim,keyLookupResult.key().get());
		}

		
		@Override
		public AuthTokenStatus validate(HashedAuthToken token) {
			
			if(token == null || token.issuedTime() == null || token.claims() == null || token.userIdentifier() == null || token.nonce() == null){
				return AuthTokenStatus.OTHER_NOT_VALID;
			}
			
			KeyLookupResultTuple keyLookupResult = rollingKeyStoreReader.get().getRollingKey(token.issuedTime());
			
			if(keyLookupResult.lookupResult() == KeyStoreLookupResult.EXPIRED){
				return AuthTokenStatus.EXPIRED;
			}
			
			return keyLookupResult.key().isPresent() && token.hash().equals(hashClaim(token,keyLookupResult.key().get())) ? 
					AuthTokenStatus.VALID : AuthTokenStatus.OTHER_NOT_VALID;
		}
		
		
		private String hashClaim(AuthTokenClaim claim,RollingKey key){
			return bytesToString(
					hashingService.get().hash(
							Bytes.concat(stringToBytes(claimToString(claim)),key.key())));
		}
		
		
		private String claimToString(AuthTokenClaim claim){
			return claim.nonce() + claim.userIdentifier() + claim.issuedTime() + Joiner.on('|').join(claim.claims());
		}
		
		
		private String bytesToString(byte[] bytes){
			return new String(Hex.encodeHex(bytes));
		}
		
		
		private byte[] stringToBytes(String string){
			return string.getBytes(CHARSET);
		}
	}
}

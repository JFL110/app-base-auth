package org.jfl110.spi.auth.rolling.utils;

import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jfl110.api.auth.HashedAuthToken;
import org.jfl110.api.time.CurrentTime;
import org.jfl110.spi.auth.utils.RandomKeyGeneratorService;
import org.joda.time.LocalDateTime;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.inject.ImplementedBy;

@ImplementedBy(HashedTokenIssuer.Impl.class)
public interface HashedTokenIssuer {
	
	Builder issue();
	
	public interface Builder{
		
		Builder userId(String userId);
		
		Builder claims(Collection<String> claims);
		
		Builder nonceLength(int length);
		
		HashedAuthToken build();
	}
	
	
	static class Impl implements HashedTokenIssuer {
		
		private static final int DEFAULT_NONCE_LENGTH = 16;
		private final Provider<RandomKeyGeneratorService> randomKeyGeneratorService;
		private final Provider<LocalDateTime> currentTimeProvider;
		private final Provider<AuthTokenRollingKeyHashService> authTokenHashService;
		
		@Inject
		Impl(Provider<RandomKeyGeneratorService> randomKeyGeneratorService,
				@CurrentTime Provider<LocalDateTime> currentTimeProvider,
				 Provider<AuthTokenRollingKeyHashService> authTokenHashService){
			this.randomKeyGeneratorService = randomKeyGeneratorService;
			this.currentTimeProvider = currentTimeProvider;
			this.authTokenHashService = authTokenHashService;
		}

		@Override
		public Builder issue() {
			return new BuilderImpl();
		}
		
		
		 class BuilderImpl implements Builder{
			
			private String userId;
			private Optional<Integer> nonceLength = Optional.absent();
			private final List<String> claims = Lists.newArrayList();
			
			@Override
			public Builder userId(String userId) {
				this.userId = userId;
				return this;
			}
			
			@Override
			public Builder claims(Collection<String> claims) {
				this.claims.addAll(claims);
				return this;
			}

			@Override
			public Builder nonceLength(int length) {
				this.nonceLength = Optional.of(length);
				return this;
			}

			@Override
			public HashedAuthToken build() {
				
				if(userId == null){
					throw new IllegalArgumentException("No user id specified for token");
				}

				String nonce = randomKeyGeneratorService.get().newHexKey(nonceLength.isPresent() ? nonceLength.get() : DEFAULT_NONCE_LENGTH);
				LocalDateTime tokenTime = currentTimeProvider.get();
				HashedAuthTokenImpl token = new HashedAuthTokenImpl(userId, nonce, tokenTime,claims);
				token.setHash(authTokenHashService.get().hashClaim(token));
				
				return token;
			}
			
		}
	}
	
	
	final class HashedAuthTokenImpl implements HashedAuthToken {

		private static final long serialVersionUID = 1L;
		
		private final String userIdentifier;
		private final String nonce;
		private final LocalDateTime issuedTime;
		private final List<String> claims;
		private String hash;

		HashedAuthTokenImpl(String userIdentifier, String nonce, LocalDateTime issuedTime, List<String> claims) {
			this.userIdentifier = userIdentifier;
			this.nonce = nonce;
			this.issuedTime = issuedTime;
			this.claims = claims;
		}

		@Override
		public String userIdentifier() {
			return userIdentifier;
		}

		@Override
		public String nonce() {
			return nonce;
		}

		@Override
		public LocalDateTime issuedTime() {
			return issuedTime;
		}

		@Override
		public List<String> claims() {
			return claims;
		}

		@Override
		public String hash() {
			return hash;
		}

		void setHash(String hash) {
			this.hash = hash;
		}
	}

}

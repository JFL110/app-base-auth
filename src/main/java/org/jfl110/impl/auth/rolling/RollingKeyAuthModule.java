package org.jfl110.impl.auth.rolling;

import org.jfl110.api.auth.rolling.RollingKeyStore;

import com.google.inject.Binder;
import com.google.inject.Module;

public class RollingKeyAuthModule implements Module{

	@Override
	public void configure(Binder binder) {
		binder.bind(RollingKeyStore.Reader.class).to(DefaultRollingKeyStore.class);
		binder.bind(RollingKeyStore.Updater.class).to(DefaultRollingKeyStoreUpdater.class);
	}

}

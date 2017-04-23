package org.jfl110.impl.time;

import javax.inject.Provider;

import org.jfl110.api.time.CurrentTime;
import org.joda.time.LocalDateTime;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * Provides the current time where a Provider<LocalDateTime> is annotated with @CurrentTime
 *
 * @author JFL110
 */
public class CurrentTimeModule implements Module{

	@Override
	public void configure(Binder binder) {
		binder.bind(LocalDateTime.class).annotatedWith(CurrentTime.class).toProvider(new Provider<LocalDateTime>(){
			@Override
			public LocalDateTime get() {
				return LocalDateTime.now();
			}
		});
	}

}

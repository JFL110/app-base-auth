package org.jfl110.api.time;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

/**
 * Binding annotation to indicate a Provider<LocalDateTime> supplies
 * the current time. This removes the need for LocalDateTime.now() and so 
 * simplifies testing.
 *
 * @author JFL110
 */
@Target({ ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface CurrentTime {

}

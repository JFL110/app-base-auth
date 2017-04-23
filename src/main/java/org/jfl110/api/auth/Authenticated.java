package org.jfl110.api.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;
 
/**
 * Binding annotation to pair with objects that are only to be injected
 * if they have been successfully authenticated, thus eliminating the need
 * for the consuming class to validate the object.
 * 
 * Individual claims made by the token may need to be verified by the consumer.
 *
 * @author JFL110
 */
@Target({ ElementType.PARAMETER,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@BindingAnnotation
public @interface Authenticated {}
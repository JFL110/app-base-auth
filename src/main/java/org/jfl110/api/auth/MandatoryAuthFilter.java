package org.jfl110.api.auth;

import javax.servlet.Filter;

/**
 * Filter to place in-front of servlets that can only be accessed 
 * if the request has supplied a valid authentication token.
 * 
 * If there is no token, or if the token is invalid, this filter will
 * return an error and prevent further processing.
 * 
 * The filter will also make the verified token available for
 * downstream filters/servlets.
 * 
 * Individual claims made by the token may not be verified by this filter.
 *
 * @author JFL110
 */
public interface MandatoryAuthFilter extends Filter{}
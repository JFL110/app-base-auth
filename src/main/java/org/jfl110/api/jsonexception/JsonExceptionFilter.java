package org.jfl110.api.jsonexception;

import javax.servlet.Filter;

/**
 * A filter which catches JsonExceptions thrown from 
 * the downstream chain and writes them to the response
 * as Json.
 * 
 * @author JFL110
 */
public interface JsonExceptionFilter extends Filter {}
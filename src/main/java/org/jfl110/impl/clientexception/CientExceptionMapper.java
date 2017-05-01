package org.jfl110.impl.clientexception;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.jfl110.api.clientexception.ClientException;

@Provider
public class CientExceptionMapper implements ExceptionMapper<ClientException>{

	@Override
	public Response toResponse(ClientException exception) {
		return Response
				.status(Response.Status.OK)
				.entity(new ErrorResponse(exception.errorCode(),exception.description()))
				.build();
	}
	
	
	public static class ErrorResponse{
		public String error;
		public String description;
		
		ErrorResponse(String error,String description){
			this.error = error;
			this.description = description;
		}
	}
}

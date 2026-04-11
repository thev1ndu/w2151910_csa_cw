package com.smartcampus.exception;

import com.smartcampus.model.ErrorMessage;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        int status = 422; // Unprocessable Entity
        ErrorMessage error = new ErrorMessage(exception.getMessage(), status);
        return Response.status(status)
                .type(MediaType.APPLICATION_JSON)
                .entity(error)
                .build();
    }
}

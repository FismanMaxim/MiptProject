package EndpointsControllers;

import Responses.ErrorResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import spark.Response;

public abstract class EndpointsController {
    //protected final Service service;
    protected final ObjectMapper objectMapper;

    public EndpointsController(/*Service service,*/ ObjectMapper objectMapper) {
        //this.service = service;
        this.objectMapper = objectMapper;
    }

    protected String InformOfClientError(Logger logger, String errorMessage, Response response, Exception cause, int statusCode) {
        logger.warn(errorMessage, cause);
        response.status(statusCode);
        return getJsonExceptionResponse(cause);
    }

    protected String getJsonExceptionResponse(Exception exception) {
        return getJsonExceptionResponse(exception.getMessage());
    }

    protected String getJsonExceptionResponse(String exceptionMessage) {
        try {
            return objectMapper.writeValueAsString(new ErrorResponse(exceptionMessage));
        } catch (JsonProcessingException e){
            throw new IllegalArgumentException();
        }
    }

    public abstract void initializeEndpoints();
}

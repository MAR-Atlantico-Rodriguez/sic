package sic.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.ResourceBundle;
import javax.persistence.EntityNotFoundException;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import sic.service.BusinessServiceException;

@ControllerAdvice
public class ControllersExceptionHandler {
    
    private final Logger LOGGER = Logger.getLogger(this.getClass());
     
    private String log(Exception ex) {
        String mensaje = ex.getMessage() + "\n(Transaction ID: " + new Date().getTime() + ")";
        if (ex.getCause() != null) {                        
            LOGGER.error(mensaje + " " + ex.getCause().getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
        } else {
            LOGGER.error(mensaje + " " + ResourceBundle.getBundle("Mensajes")
                    .getString("mensaje_error_sin_causa") + "\n" + Arrays.toString(ex.getStackTrace()));
        }
        return mensaje;
    }
    
    @ExceptionHandler(BusinessServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBusinessServiceException(BusinessServiceException ex) {        
        return log(ex);
    }
    
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    public String handleUnauthorizedException(UnauthorizedException ex) {
        return log(ex);        
    }       
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleEntityNotFoundException(EntityNotFoundException ex) {        
        return log(ex);
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public String handleException(Exception ex) {
        return log(new Exception(ResourceBundle.getBundle("Mensajes").getString("mensaje_error_request"), ex));
    }
}

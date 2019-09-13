package com.ryanair.searchflight.exception;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * @author sergiopf
 */
@ControllerAdvice
public class ResponseExceptionHandler extends ResponseEntityExceptionHandler {
 
	private static final Logger log = LogManager.getLogger(ResponseExceptionHandler.class);
	
    @ExceptionHandler({ RuntimeException.class })
    public final ResponseEntity<Object> handleRuntimeException(RuntimeException ex) {
    	log.error(ex);
        return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }
    
    @Override
    protected ResponseEntity<Object> handleBindException(
			BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
 
    	log.error(ex);
    	Map<String, Object> body = new LinkedHashMap<>();
    	List<Map> errosList = new ArrayList<>();
        for (FieldError error: ex.getBindingResult().getFieldErrors()) {
        	Map<String, String> errorObject = new LinkedHashMap<>();
        	errorObject.put("field", error.getField());
        	errorObject.put("error", error.getDefaultMessage());
        	errosList.add(errorObject);
        }
        body.put("errors",errosList);
        return new ResponseEntity<>(body, headers, status);
    }
    
}
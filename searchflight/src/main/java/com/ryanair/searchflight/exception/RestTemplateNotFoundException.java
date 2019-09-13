package com.ryanair.searchflight.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * @author sergiopf
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class RestTemplateNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -685671492888906763L;

	public RestTemplateNotFoundException(String message) {
        super(message);
    }
}
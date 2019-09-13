package com.ryanair.searchflight.exception;

import java.io.IOException;
import java.net.URI;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * @author sergiopf
 */
@Component
public class RestTemplateErrorHandler implements ResponseErrorHandler {
 
    @Override
    public boolean hasError(ClientHttpResponse httpResponse) 
      throws IOException {
        return (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR 
          || httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR);
    }
 
    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse httpResponse) 
      throws IOException {
 
        if (httpResponse.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR) {
        	throw new RuntimeException("Server Error");
        } else if (httpResponse.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR) {
            if (httpResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RestTemplateNotFoundException(url.toString());
            }
        }
    }
    
    @Override
    public void handleError(ClientHttpResponse httpResponse) 
      throws IOException {
    }
}

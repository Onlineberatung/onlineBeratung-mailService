package de.caritas.cob.mailservice.api;

import de.caritas.cob.mailservice.api.exception.InternalServerErrorException;
import de.caritas.cob.mailservice.api.service.LogService;
import java.net.UnknownHostException;
import javax.validation.ConstraintViolationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Customizes API error/exception handling to hide information and/or possible security
 * vulnerabilities.
 *
 */
@NoArgsConstructor
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

  /**
   * 
   * Handle all common "Bad Request" errors (400)
   * 
   */

  /**
   * Constraint violations
   * 
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<Object> handleBadRequest(final RuntimeException ex,
      final WebRequest request) {
    LogService.logWarn(ex);

    return handleExceptionInternal(null, null, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
  }

  /**
   * Incoming request body could not be deserialized
   */
  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      final HttpMessageNotReadableException ex, final HttpHeaders headers, final HttpStatus status,
      final WebRequest request) {
    LogService.logWarn(status, ex);

    return handleExceptionInternal(null, null, headers, status, request);
  }

  /**
   * @Valid on object fails validation
   */
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      final MethodArgumentNotValidException ex, final HttpHeaders headers, final HttpStatus status,
      final WebRequest request) {
    LogService.logWarn(status, ex);

    return handleExceptionInternal(null, null, headers, status, request);
  }

  /**
   * {@link RestTemplate} API client errors
   */
  @ExceptionHandler({HttpClientErrorException.class})
  protected ResponseEntity<Object> handleHttpClientException(final HttpClientErrorException ex,
      final WebRequest request) {
    LogService.logWarn(ex.getStatusCode(), ex);

    return handleExceptionInternal(null, null, new HttpHeaders(), ex.getStatusCode(), request);
  }

  /**
   * 500 - Internal Server Error
   * 
   * @param ex
   * @param request
   * @return
   */
  @ExceptionHandler({NullPointerException.class, IllegalArgumentException.class,
      IllegalStateException.class, UnknownHostException.class, InternalServerErrorException.class})
  public ResponseEntity<Object> handleInternal(final RuntimeException ex,
      final WebRequest request) {
    LogService.logError(ex);

    return handleExceptionInternal(null, null, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR,
        request);
  }

}

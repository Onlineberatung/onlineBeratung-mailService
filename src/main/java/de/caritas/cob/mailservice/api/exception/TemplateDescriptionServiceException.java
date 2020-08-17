package de.caritas.cob.mailservice.api.exception;

public class TemplateDescriptionServiceException extends Exception {

  /**
   * Template description service exception
   *
   * @param message
   * @param ex
   */
  public TemplateDescriptionServiceException(String message, Exception ex) {
    super(message, ex);
  }

}

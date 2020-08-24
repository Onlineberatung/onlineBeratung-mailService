package de.caritas.cob.mailservice.api.exception;

public class ExchangeMailServiceException extends Exception {

  /**
   * Exchange mail service exception.
   *
   * @param ex
   */
  public ExchangeMailServiceException(Exception ex) {
    super(ex);
  }

  /**
   * Exchange mail service exception.
   *
   * @param message
   * @param ex
   */
  public ExchangeMailServiceException(String message, Exception ex) {
    super(message, ex);
  }

  /**
   * Exchange mail service exception.
   *
   * @param message
   */
  public ExchangeMailServiceException(String message) {
    super(message);
  }

}

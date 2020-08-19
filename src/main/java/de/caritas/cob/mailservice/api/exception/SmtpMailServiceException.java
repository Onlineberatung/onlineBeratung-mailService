package de.caritas.cob.mailservice.api.exception;

public class SmtpMailServiceException extends Exception {

  /**
   * Smtp mail service exception.
   *
   * @param ex
   */
  public SmtpMailServiceException(Exception ex) {
    super(ex);
  }

  /**
   * Smtp mail service exception.
   *
   * @param message
   * @param ex
   */
  public SmtpMailServiceException(String message, Exception ex) {
    super(message, ex);
  }

  /**
   * Smtp mail service exception.
   *
   * @param message
   */
  public SmtpMailServiceException(String message) {
    super(message);
  }

}

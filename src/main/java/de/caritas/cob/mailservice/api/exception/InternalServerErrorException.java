package de.caritas.cob.mailservice.api.exception;

/**
 * Server error exception to return status code 500
 */
public class InternalServerErrorException extends RuntimeException {

  /**
   * Internal server error
   * @param message
   */
  public InternalServerErrorException(String message) {
    super(message);
  }

  /**
   * Internal server error
   * @param message
   * @param e
   */
  public InternalServerErrorException(String message, Exception e) {
    super(message, e);
  }

}

package de.caritas.cob.MailService.api.exception;

public class ServiceException extends RuntimeException {

  private static final long serialVersionUID = -3638082430453921781L;

  /**
   * Service exception
   * 
   * @param ex
   */
  public ServiceException(Exception ex) {
    super(ex);
  }

  /**
   * Service exception
   * 
   * @param message
   * @param ex
   */
  public ServiceException(String message, Exception ex) {
    super(message, ex);
  }

  /**
   * Service exception
   * 
   * @param message
   */
  public ServiceException(String message) {
    super(message);
  }



}

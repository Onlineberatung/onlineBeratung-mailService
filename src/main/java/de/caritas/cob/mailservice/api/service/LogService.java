package de.caritas.cob.mailservice.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * Service for logging.
 */
public class LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

  private LogService() {
  }

  /**
   * Logs a an error with exception.
   *
   * @param message an error message
   * @param exception the exception
   */
  public static void logError(String message, Exception exception) {
    LOGGER.error("Mail service error: {}", message);
    LOGGER.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception));
  }

  /**
   * Logs a an error with exception.
   *
   * @param exception the exception
   */
  public static void logError(Exception exception) {
    LOGGER.error("Mail service error:");
    LOGGER.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception));
  }

  /**
   * Logs a an error message.
   *
   * @param message an error message
   */
  public static void logError(String message) {
    LOGGER.error("Mail service error: {}", message);
  }

  /**
   * Log a debug message.
   *
   * @param message the message to be logged
   */
  public static void logDebug(String message) {
    LOGGER.debug(message);
  }

  /**
   * Log a warning message.
   *
   * @param message the message to be logged
   */
  public static void logWarn(String message) {
    LOGGER.warn(message);
  }

  /**
   * Log a warning message.
   *
   * @param status http status to be logged
   * @param exception the exception to be logged
   */
  public static void logWarn(HttpStatus status, Exception exception) {
    LOGGER.warn("MailService API: {}: {}", status.getReasonPhrase(),
        org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception));
  }

  /**
   * Log a warning message.
   *
   * @param exception the exception
   */
  public static void logWarn(Exception exception) {
    LOGGER.warn("MailService API: {}: ",
        org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception));
  }

}

package de.caritas.cob.mailservice.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for logging
 */
public class LogService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LogService.class);

  private LogService() {}

  /**
   * Logs a an error with exception
   *
   * @param message an error message
   * @param exception the exception
   */
  public static void logError(String message, Exception exception) {
    LOGGER.error("Mail service error: {}", message);
    LOGGER.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception));
  }

  /**
   * Logs a an error message
   *
   * @param message an error message
   */
  public static void logError(String message) {
    LOGGER.error("Mail service error: {}", message);
  }

  /**
   * Log a debug message
   */
  public static void logDebug(String message) {
    LOGGER.debug(message);
  }

  /**
   * Log a warning message
   */
  public static void logWarn(String message) {
    LOGGER.warn(message);
  }

}

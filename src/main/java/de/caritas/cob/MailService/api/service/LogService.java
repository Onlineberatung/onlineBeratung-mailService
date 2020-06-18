package de.caritas.cob.MailService.api.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for logging
 */

@Service
@Slf4j
public class LogService {

  @Autowired
  private SmtpMailService smtpMailService;

  @Autowired
  private ExchangeMailService exchangeMailService;

  @Value("${mail.usesmtp}")
  private boolean useSMTP;

  @Value("${mail.error.recipients}")
  private String errorRecipients;

  /**
   * Logs a an error with exception
   * 
   * @param message an error message
   * @param exception the exception
   */
  public void logError(String message, Exception exception) {
    log.error("Mail service error: {}", message);
    log.error(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception));
    StringBuilder stringBuilderMailBody = new StringBuilder();
    stringBuilderMailBody.append("Error message:" + message + "\n");
    stringBuilderMailBody
        .append(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(exception));
    sendErrorMail(stringBuilderMailBody.toString());
  }

  /**
   * Logs a an error message
   * 
   * @param message an error message
   */
  public void logError(String message) {
    log.error("Mail service error: {}", message);
    sendErrorMail(message);
  }

  private void sendErrorMail(String body) {
    if (errorRecipients != null && !errorRecipients.trim().equals(StringUtils.EMPTY)) {
      if (useSMTP) {
        smtpMailService.prepareAndSendTextMail(errorRecipients,
            "Caritas Online Beratung: An error occurred while sending the mail via the mail service.",
            body);
      } else {
        exchangeMailService.prepareAndSendTextMail(errorRecipients,
            "Caritas Online Beratung: An error occurred while sending the mail via the mail service.",
            body);
      }
    } else {
      log.warn("Error mail was not send, because no error recipient is set.");
    }
  }

  /**
   * Log a debug message
   * 
   * @param message
   */
  public void logDebug(String message) {
    log.debug(message);
  }

}

package de.caritas.cob.mailservice.api.service;

import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

import de.caritas.cob.mailservice.api.model.MailDTO;

/**
 * Builds a message for an occured error and filters possible existing email addresses from the
 * stacktrace.
 */
public class MailErrorMessageBuilder {

  private static final String SIMPLE_EMAIL_PATTERN = "\\w+@\\w+\\.\\w+";

  /**
   * Builds an error mail message and filteres possible email addresses from the stacktrace.
   *
   * @param mail the mail where the error occured
   * @param ex the exception
   * @return the generated error message without email addresses
   */
  public String buildEmailErrorMessage(MailDTO mail, Exception ex) {
    String errorMessage = String.format("Mail request for template %s could not be executed.",
        mail.getTemplate());

    return "Error message:"
        + errorMessage + "\n"
        + replaceMailAddresses(getStackTrace(ex));
  }

  private String replaceMailAddresses(String stacktrace) {
    return stacktrace.replaceAll(SIMPLE_EMAIL_PATTERN, "");
  }

}

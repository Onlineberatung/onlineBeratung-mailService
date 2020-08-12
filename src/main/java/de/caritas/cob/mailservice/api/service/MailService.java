package de.caritas.cob.mailservice.api.service;

import de.caritas.cob.mailservice.api.exception.ServiceException;
import de.caritas.cob.mailservice.api.helper.TemplateDataConverter;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import de.caritas.cob.mailservice.api.model.MailDTO;
import de.caritas.cob.mailservice.api.model.MailsDTO;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
@RequiredArgsConstructor
public class MailService {

  private final @NonNull SmtpMailService smtpMailService;
  private final @NonNull ExchangeMailService exchangeMailService;
  private final @NonNull TemplateDescriptionService templateDescriptionService;
  private final @NonNull TemplateService templateService;
  private final @NonNull TemplateDataConverter templateDataConverter;

  @Value("${mail.usesmtp}")
  private boolean useSMTP;

  @Value("${mail.error.recipients}")
  private String errorRecipients;

  /**
   * sends all mails as html
   */
  public void sendHtmlMails(MailsDTO mailsDTO) {
    if (!CollectionUtils.isEmpty(mailsDTO.getMails())) {
      mailsDTO.getMails().forEach(processAndSendHtmlMail());
    }
  }

  private Consumer<MailDTO> processAndSendHtmlMail() {
    return mailDTO -> {
      try {
        Optional<TemplateDescription> optionalTemplateDescription =
            templateDescriptionService.getTemplateDescription(mailDTO.getTemplate());
        if (!optionalTemplateDescription.isPresent()) {
          logAndSendErrorMessage(mailDTO);
        } else {
          loadRequiredMailDataAndSendMail(mailDTO, optionalTemplateDescription.get());
        }
      } catch (ServiceException ex) {
        handleMailSendFailure(mailDTO, ex);
      }
    };
  }

  private void logAndSendErrorMessage(MailDTO mail) {
    String errorMessage = String
        .format("Template description %s could not be found.", mail.getTemplate());
    LogService.logError(errorMessage);
    sendErrorMail(errorMessage);
  }

  private void loadRequiredMailDataAndSendMail(MailDTO mail,
      TemplateDescription templateDescription) {
    Map<String, Object> templateData =
        templateDataConverter.convertFromTemplateDataDTOList(mail.getTemplateData());

    Optional<String> optionalProcessedHtmlTemplate = templateService
        .getProcessedHtmlTemplate(templateDescription, mail.getTemplate(), templateData);

    String subject = templateService.getProcessedSubject(templateDescription, templateData);

    optionalProcessedHtmlTemplate
        .ifPresent(template -> sendHtmlMail(mail, templateDescription, template, subject));
  }

  private void sendHtmlMail(MailDTO mail, TemplateDescription templateDescription,
      String processedHtmlTemplate, String subject) {
    if (useSMTP) {
      smtpMailService.prepareAndSendHtmlMail(mail.getEmail(), subject,
          processedHtmlTemplate, templateDescription.getTemplateImages());
    } else {
      exchangeMailService.prepareAndSendHtmlMail(mail.getEmail(), subject,
          processedHtmlTemplate, templateDescription.getTemplateImages());
    }
  }

  private void handleMailSendFailure(MailDTO mail, ServiceException ex) {
    String errorMessage = String.format("Mail request for template %s could not be executed.",
        mail.getTemplate());
    LogService.logError(errorMessage, ex);
    StringBuilder stringBuilderMailBody = new StringBuilder();
    stringBuilderMailBody.append("Error message:" + errorMessage + "\n");
    stringBuilderMailBody
        .append(org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace(ex));
    sendErrorMail(stringBuilderMailBody.toString());
  }

  /**
   * sens an error mail
   */
  public void sendErrorMail(String body) {
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
      LogService.logWarn("Error mail was not send, because no error recipient is set.");
    }
  }

}

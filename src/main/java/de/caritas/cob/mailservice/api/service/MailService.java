package de.caritas.cob.mailservice.api.service;

import static java.util.Objects.nonNull;

import de.caritas.cob.mailservice.api.exception.ExchangeMailServiceException;
import de.caritas.cob.mailservice.api.exception.InternalServerErrorException;
import de.caritas.cob.mailservice.api.exception.SmtpMailServiceException;
import de.caritas.cob.mailservice.api.exception.TemplateDescriptionServiceException;
import de.caritas.cob.mailservice.api.exception.TemplateServiceException;
import de.caritas.cob.mailservice.api.helper.TemplateDataConverter;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import de.caritas.cob.mailservice.api.model.ErrorMailDTO;
import de.caritas.cob.mailservice.api.model.MailDTO;
import de.caritas.cob.mailservice.api.model.MailsDTO;
import de.caritas.cob.mailservice.api.model.TemplateDataDTO;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
  private boolean useSmtp;

  @Value("${mail.error.recipients}")
  private String errorRecipients;

  /**
   * sends all mails as html.
   *
   * @param mailsDTO the mails to be send
   */
  public void sendHtmlMails(MailsDTO mailsDTO) {
    if (!CollectionUtils.isEmpty(mailsDTO.getMails())) {
      mailsDTO.getMails().forEach(processAndSendHtmlMail());
    }
  }

  private Consumer<MailDTO> processAndSendHtmlMail() {
    return mailDTO -> {
      try {
        templateDescriptionService.getTemplateDescription(mailDTO.getTemplate()).ifPresentOrElse(
            desc -> loadRequiredMailDataAndSendMail(mailDTO, desc),
            () -> logAndSendErrorMessage(mailDTO)
        );
      } catch (TemplateDescriptionServiceException ex) {
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

    Optional<String> optionalProcessedHtmlTemplate;
    try {
      optionalProcessedHtmlTemplate = templateService.getProcessedHtmlTemplate(
          templateDescription, mail.getTemplate(), templateData, mail.getLanguage()
      );
    } catch (TemplateServiceException e) {
      throw new InternalServerErrorException(
          String.format("Could not load template: %s", e.getMessage()), e);
    }

    var subject = templateService.getProcessedSubject(
        templateDescription, templateData, mail.getLanguage()
    );

    optionalProcessedHtmlTemplate
        .ifPresent(template -> sendHtmlMail(mail, templateDescription, template, subject));
  }

  private void sendHtmlMail(MailDTO mail, TemplateDescription templateDescription,
      String processedHtmlTemplate, String subject) {
    try {
      if (useSmtp) {
        smtpMailService.prepareAndSendHtmlMail(mail.getEmail(), subject,
            processedHtmlTemplate, templateDescription.getTemplateImages());
      } else {
        exchangeMailService.prepareAndSendHtmlMail(mail.getEmail(), subject,
            processedHtmlTemplate, templateDescription.getTemplateImages());
      }
    } catch (SmtpMailServiceException | ExchangeMailServiceException e) {
      throw new InternalServerErrorException(
          String.format("Could not send HTML mail: %s", e.getMessage()), e);
    }
  }

  private void handleMailSendFailure(MailDTO mail, Exception ex) {
    String errorMessage = new MailErrorMessageBuilder().buildEmailErrorMessage(mail, ex);
    LogService.logError(errorMessage);
    sendErrorMail(errorMessage);
  }

  /**
   * sends an error mail.
   *
   * @param body the mail body
   */
  public void sendErrorMail(String body) {
    if (nonNull(errorRecipients) && !errorRecipients.trim().equals(StringUtils.EMPTY)) {
      sendErrorMailWithCheckedRecipients(body);
    } else {
      LogService.logWarn("Error mail was not send, because no error recipient is set.");
    }
  }

  private void sendErrorMailWithCheckedRecipients(String body) {
    try {
      String errorMessage = "Caritas Online Beratung: An error occurred while sending the mail via the mail service.";
      if (useSmtp) {
        smtpMailService.prepareAndSendTextMail(errorRecipients, errorMessage, body);
      } else {
        exchangeMailService.prepareAndSendTextMail(errorRecipients, errorMessage, body);
      }
    } catch (SmtpMailServiceException | ExchangeMailServiceException e) {
      throw new InternalServerErrorException(e.getMessage());
    }
  }

  /**
   * Sends an error mail to all configured recipients.
   *
   * @param errorMailDTO the input {@link ErrorMailDTO}
   */
  public void sendErrorMailDto(ErrorMailDTO errorMailDTO) {
    MailDTO mailDTO = new MailDTO()
        .template(errorMailDTO.getTemplate())
        .email(this.errorRecipients)
        .templateData(errorMailDTO.getTemplateData());

    try {
      templateDescriptionService.getTemplateDescription(mailDTO.getTemplate())
          .ifPresent(templateDescription -> loadUnescapedMailDataAndSendMail(mailDTO,
              templateDescription));
    } catch (TemplateDescriptionServiceException e) {
      handleMailSendFailure(mailDTO, e);
    }
  }

  private void loadUnescapedMailDataAndSendMail(MailDTO mail, TemplateDescription desc) {
    Map<String, Object> templateData = mail.getTemplateData()
        .stream()
        .collect(Collectors.toMap(TemplateDataDTO::getKey, TemplateDataDTO::getValue));

    var subject = templateService.getProcessedSubject(desc, templateData, mail.getLanguage());
    try {
      templateService
          .getProcessedHtmlTemplate(desc, mail.getTemplate(), templateData, mail.getLanguage())
          .ifPresent(template -> sendHtmlMail(mail, desc, template, subject));
    } catch (TemplateServiceException e) {
      throw new InternalServerErrorException(
          String.format("Could not load template: %s", e.getMessage()), e);
    }
  }
}

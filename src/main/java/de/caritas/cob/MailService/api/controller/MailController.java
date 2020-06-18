package de.caritas.cob.MailService.api.controller;

import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import de.caritas.cob.MailService.api.exception.ServiceException;
import de.caritas.cob.MailService.api.helper.TemplateDataConverter;
import de.caritas.cob.MailService.api.mailTemplate.TemplateDescription;
import de.caritas.cob.MailService.api.model.MailDTO;
import de.caritas.cob.MailService.api.model.MailsDTO;
import de.caritas.cob.MailService.api.service.ExchangeMailService;
import de.caritas.cob.MailService.api.service.LogService;
import de.caritas.cob.MailService.api.service.SmtpMailService;
import de.caritas.cob.MailService.api.service.TemplateDescriptionService;
import de.caritas.cob.MailService.api.service.TemplateService;
import de.caritas.cob.MailService.generated.api.controller.MailsApi;
import io.swagger.annotations.Api;

/**
 * Controller for mail requests
 */
@RestController
@Api(tags = "mails-controller")
public class MailController implements MailsApi {

  @Autowired
  private LogService logService;

  @Autowired
  private SmtpMailService smtpMailService;

  @Autowired
  private TemplateDescriptionService templateDescriptionService;

  @Autowired
  private TemplateService templateService;

  @Autowired
  private TemplateDataConverter templateDataConverter;

  @Autowired
  private ExchangeMailService exchangeMailService;

  @Value("${mail.usesmtp}")
  private boolean useSMTP;

  /**
   * Entry point for mail sending
   */
  @Override
  public ResponseEntity<Void> sendMails(@Valid @RequestBody MailsDTO mails) {

    for (MailDTO mail : mails.getMails()) {

      try {

        Optional<TemplateDescription> optionalTemplateDescription =
            templateDescriptionService.getTemplateDescription(mail.getTemplate());
        if (!optionalTemplateDescription.isPresent()) {
          logService.logError(
              String.format("Template description %s could not be found.", mail.getTemplate()));
          continue;
        }

        TemplateDescription templateDescription = optionalTemplateDescription.get();

        Map<String, Object> templateData =
            templateDataConverter.convertFromTemplateDataDTOList(mail.getTemplateData());

        Optional<String> optionalProcessedHtmlTemplate = templateService
            .getProcessedHtmlTemplate(templateDescription, mail.getTemplate(), templateData);

        String subject = templateService.getProcessedSubject(templateDescription, templateData);

        if (optionalProcessedHtmlTemplate.isPresent()) {
          if (useSMTP) {
            smtpMailService.prepareAndSendHtmlMail(mail.getEmail(), subject,
                optionalProcessedHtmlTemplate.get(), templateDescription.getTemplateImages());
          } else {
            exchangeMailService.prepareAndSendHtmlMail(mail.getEmail(), subject,
                optionalProcessedHtmlTemplate.get(), templateDescription.getTemplateImages());
          }
        }

      } catch (ServiceException ex) {
        logService.logError(String.format("Mail request for template %s could not be executed.",
            mail.getTemplate()), ex);
      }

    }

    return new ResponseEntity<Void>(HttpStatus.OK);

  }

}

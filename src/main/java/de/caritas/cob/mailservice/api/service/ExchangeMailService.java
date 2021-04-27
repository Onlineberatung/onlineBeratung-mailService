package de.caritas.cob.mailservice.api.service;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.caritas.cob.mailservice.api.exception.ExchangeMailServiceException;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for sending mails via exchange
 */
@Service
public class ExchangeMailService {

  private static final String TEMPLATE_IMAGE_DIR = "/templates/images/";
  private static final String NEW_TEMPLATE_IMAGE_DIR = "images/";


  @Value("${mail.sender}")
  private String mailSender;

  @Value("${mail.fix.recipient}")
  private String fixMailRecipient;

  @Value("${mail.exchange.url}")
  String exchangeUrl;

  @Value("${mail.exchange.user}")
  String exchangeUser;

  @Value("${mail.exchange.password}")
  String exchangePassword;

  @Value("${mail.exchange.version}")
  String exchangeVersion;

  @Value("${resourcePath}")
  private String resourcePath;

  @Value("${newResources}")
  private boolean newResources;

  /**
   * Preparing and sending an html mail via Exchange.
   *
   * @param recipient    The mail address of the recipient
   * @param subject      The subject of the mail
   * @param htmlTemplate The name of the html template
   */
  public void prepareAndSendHtmlMail(String recipient, String subject, String htmlTemplate,
      List<TemplateImage> templateImages) throws ExchangeMailServiceException {
    this.prepareAndSendMail(recipient, subject, htmlTemplate, templateImages, BodyType.HTML);
  }

  /**
   * Preparing and sending an text mail via Exchange.
   *
   * @param recipients The mail address of the recipients
   * @param subject    The subject of the mail
   * @param body       The text to send
   */
  public void prepareAndSendTextMail(String recipients, String subject, String body)
      throws ExchangeMailServiceException {
    this.prepareAndSendMail(recipients, subject, body, null, BodyType.Text);
  }

  private void prepareAndSendMail(String recipients, String subject, String bodyText,
      List<TemplateImage> templateImages, BodyType bodyType) throws ExchangeMailServiceException {

    if (isNull(mailSender)) {
      throw new ExchangeMailServiceException("No sender mail address set");
    }

    ExchangeService exchangeService = new ExchangeService(ExchangeVersion.valueOf(exchangeVersion));
    setupExchangeService(exchangeService);
    EmailMessage msg = buildEmailMessage(subject, bodyText, bodyType, exchangeService);
    addEmailAttachmentsIfNecessary(templateImages, msg);
    setMailRecipients(recipients, msg);

    try {
      msg.send();
      LogService.logDebug("email sent");
    } catch (Exception e) {
      throw new ExchangeMailServiceException(
          String.format("Error while sending Exchange email: %s", msg.toString()), e);
    } finally {
      exchangeService.close();
    }
  }

  private void setupExchangeService(ExchangeService exchangeService)
      throws ExchangeMailServiceException {

    exchangeService.setCredentials(new WebCredentials(this.exchangeUser, this.exchangePassword));

    try {
      exchangeService.setUrl(new URI(this.exchangeUrl));
    } catch (URISyntaxException e) {
      exchangeService.close();
      throw new ExchangeMailServiceException(
          String.format("Could not set ExchangeMailService URL %s ", this.exchangeUrl), e);
    }
  }

  private EmailMessage buildEmailMessage(String subject, String bodyText, BodyType bodyType,
      ExchangeService exchangeService) throws ExchangeMailServiceException {
    try {
      EmailMessage msg = new EmailMessage(exchangeService);
      msg.setSubject(subject);

      MessageBody messageBody = new MessageBody();
      messageBody.setBodyType(bodyType);
      messageBody.setText(bodyText);
      msg.setBody(messageBody);
      return msg;
    } catch (Exception e) {
      throw new ExchangeMailServiceException("Could not prepare message data (subject / body)", e);
    }
  }

  private void addEmailAttachmentsIfNecessary(List<TemplateImage> templateImages, EmailMessage msg)
      throws ExchangeMailServiceException {
    if (!CollectionUtils.isEmpty(templateImages)) {
      try {
        int attachmentIndex = 0;
        for (TemplateImage templateImage : templateImages) {
          InputStream inputStream =
              newResources ? new FileInputStream(
                  resourcePath + NEW_TEMPLATE_IMAGE_DIR + templateImage.getFilename()) : getClass()
                  .getResourceAsStream(TEMPLATE_IMAGE_DIR + templateImage.getFilename());
          msg.getAttachments().addFileAttachment(templateImage.getFilename(), inputStream);
          msg.getAttachments().getItems().get(attachmentIndex).setIsInline(true);
          msg.getAttachments().getItems().get(attachmentIndex)
              .setContentId(templateImage.getFilename());
          msg.getAttachments().getItems().get(attachmentIndex).setName(templateImage.getFilename());
          msg.getAttachments().getItems().get(attachmentIndex)
              .setContentType(templateImage.getFiletype());
          attachmentIndex++;
        }
      } catch (Exception e) {
        throw new ExchangeMailServiceException("Error while processing attachments", e);
      }
    }
  }

  private void setMailRecipients(String recipients, EmailMessage msg)
      throws ExchangeMailServiceException {
    try {
      if (isNotBlank(fixMailRecipient)) {
        // fixMailRecipient is present - send to fixMailRecipient
        msg.getToRecipients().add(fixMailRecipient);
      } else {
        // No fixMailRecipient present - send to original recipient
        for (String recipient : recipients.split(",")) {
          msg.getToRecipients().add(recipient);
        }
      }
    } catch (Exception e) {
      throw new ExchangeMailServiceException("Could not set recipient", e);
    }
  }

}

package de.caritas.cob.MailService.api.service;

import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import de.caritas.cob.MailService.api.exception.ServiceException;
import de.caritas.cob.MailService.api.mailTemplate.TemplateImage;
import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.enumeration.property.BodyType;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

/**
 * Service for sending mails via exchange
 */
@Service
public class ExchangeMailService {

  private LogService logService;

  private final String TEMPLATE_IMAGE_DIR = "/templates/images/";

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

  @Autowired
  public ExchangeMailService(LogService logService) {
    this.logService = logService;
  }

  /**
   * Preparing and sending an html mail via Exchange.
   *
   * @param recipient The mail address of the recipient
   * @param subject The subject of the mail
   * @param htmlTemplate The name of the html template
   */
  public void prepareAndSendHtmlMail(String recipient, String subject, String htmlTemplate,
      List<TemplateImage> templateImages) {
    this.prepareAndSendMail(recipient, subject, htmlTemplate, templateImages, BodyType.HTML);
  }

  /**
   * Preparing and sending an text mail via Exchange
   * 
   * @param recipient The mail address of the recipient
   * @param subject The subject of the mail
   * @param body The text to send
   */
  public void prepareAndSendTextMail(String recipient, String subject, String body) {
    this.prepareAndSendMail(recipient, subject, body, null, BodyType.Text);
  }

  /**
   * Preparing and sending an html mail via smtp.
   * 
   * @param recipient The mail address of the recipient
   * @param subject The subject of the mail
   * @param bodyText The name of the html template
   * @param bodyType The bodyType you want to send
   */
  public void prepareAndSendMail(String recipient, String subject, String bodyText,
      List<TemplateImage> templateImages, BodyType bodyType) {

    if (mailSender == null) {
      throw new ServiceException("No sender mail address set");
    }

    ExchangeService exchangeService = new ExchangeService(ExchangeVersion.valueOf(exchangeVersion));
    exchangeService.setCredentials(new WebCredentials(this.exchangeUser, this.exchangePassword));

    try {
      exchangeService.setUrl(new URI(this.exchangeUrl));
    } catch (URISyntaxException e) {
      exchangeService.close();
      throw new ServiceException(
          String.format("Could not set ExchangeMailService URL %s ", this.exchangeUrl), e);
    }

    // Message Object
    EmailMessage msg = null;

    try {
      msg = new EmailMessage(exchangeService);
      msg.setSubject(subject);

      MessageBody messageBody = new MessageBody();
      messageBody.setBodyType(bodyType);
      messageBody.setText(bodyText);
      msg.setBody(messageBody);
    } catch (Exception e) {
      throw new ServiceException("Could not prepare message data (subject / body)", e);
    }

    // Create Message Attachments if necessary
    if (templateImages != null && !templateImages.isEmpty()) {
      try {
        int attachmentIndex = 0;
        for (TemplateImage templateImage : templateImages) {
          InputStream inputStream =
              getClass().getResourceAsStream(TEMPLATE_IMAGE_DIR + templateImage.getFilename());
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
        throw new ServiceException("Error while processing attachments", e);
      }
    }

    // Set Recipient
    try {
      if (fixMailRecipient != null && !fixMailRecipient.equals(StringUtils.EMPTY)) {
        // fixMailRecipient is present - send to fixMailRecipient
        msg.getToRecipients().add(fixMailRecipient);
      } else {
        // No fixMailRecipient present - send to original recipient
        msg.getToRecipients().add(recipient);
      }
    } catch (Exception e) {
      throw new ServiceException("Could not set recipient", e);
    }

    // Send mail
    try {
      msg.send();
      logService.logDebug("email sent");
    } catch (Exception e) {
      throw new ServiceException("Error while sending email", e);
    }

    exchangeService.close();

  }

}

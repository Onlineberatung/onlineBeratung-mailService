package de.caritas.cob.mailservice.api.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import de.caritas.cob.mailservice.api.exception.ServiceException;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateImage;

/**
 * Service for sending mails via smtp
 */
@Service
public class SmtpMailService {

  private static final String TEMPLATE_IMAGE_DIR = "/templates/images/";

  private JavaMailSender javaMailSender;

  @Value("${mail.sender}")
  private String mailSender;

  @Value("${mail.fix.recipient}")
  private String fixMailRecipient;

  /**
   * Standard constructor for mail service
   * 
   * @param javaMailSender
   */
  @Autowired
  public SmtpMailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  /**
   * Preparing and sending an html mail via smtp.
   * 
   * @param recipient The mail address of the recipient
   * @param subject The subject of the mail
   * @param htmlTemplate The name of the html template
   */
  public void prepareAndSendHtmlMail(String recipient, String subject, String htmlTemplate,
      List<TemplateImage> templateImages) {

    if (mailSender == null) {
      throw new ServiceException("No sender mail address set");
    }

    MimeMessagePreparator messagePreparator = mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
          (templateImages != null && !templateImages.isEmpty()), "UTF-8");
      messageHelper.setFrom(this.mailSender);
      if (fixMailRecipient != null && !fixMailRecipient.equals(StringUtils.EMPTY)) {
        messageHelper.setTo(fixMailRecipient);
      } else {
        messageHelper.setTo(recipient);
      }
      messageHelper.setSubject(subject);
      messageHelper.setText(htmlTemplate, true);

      if (templateImages != null && !templateImages.isEmpty()) {
        for (TemplateImage templateImage : templateImages) {
          messageHelper.addInline(templateImage.getFilename(),
              new ClassPathResource(TEMPLATE_IMAGE_DIR + templateImage.getFilename()),
              templateImage.getFiletype());
        }
      }

    };

    try {
      javaMailSender.send(messagePreparator);
    } catch (MailException ex) {
      throw new ServiceException("Mail could not be send", ex);
    }

  }

  /**
   * 
   * Preparing and sending an simple text mail.
   * 
   * @param recipient The mail address of the recipient
   * @param subject The subject of the mail
   * @param body The body of the mail
   */
  public void prepareAndSendTextMail(String recipient, String subject, String body) {

    if (mailSender == null) {
      throw new ServiceException("No sender mail address set");
    }

    MimeMessagePreparator messagePreparator = mimeMessage -> {

      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom(this.mailSender);
      if (fixMailRecipient != null && !fixMailRecipient.equals(StringUtils.EMPTY)) {
        messageHelper.setTo(fixMailRecipient);
      } else {
        String[] recipients = recipient.split(",");
        messageHelper.setTo(recipients);
      }
      messageHelper.setSubject(subject);
      messageHelper.setText(body, false);
    };

    try {
      javaMailSender.send(messagePreparator);
    } catch (MailException ex) {
      throw new ServiceException("Mail could not be send", ex);
    }

  }

}

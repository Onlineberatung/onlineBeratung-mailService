package de.caritas.cob.mailservice.api.service;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import de.caritas.cob.mailservice.api.exception.SmtpMailServiceException;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateImage;
import java.io.FileInputStream;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for sending mails via smtp
 */
@Service
public class SmtpMailService {

  private static final String TEMPLATE_IMAGE_DIR = "/templates/images/";
  private static final String NEW_TEMPLATE_IMAGE_DIR = "images/";

  private JavaMailSender javaMailSender;

  @Value("${mail.sender}")
  private String mailSender;

  @Value("${mail.fix.recipient}")
  private String fixMailRecipient;

  @Value("${resourcePath}")
  private String resourcePath;

  @Value("${newResources}")
  private boolean newResources;

  /**
   * Standard constructor for mail service
   */
  @Autowired
  public SmtpMailService(JavaMailSender javaMailSender) {
    this.javaMailSender = javaMailSender;
  }

  /**
   * Preparing and sending an html mail via smtp.
   *
   * @param recipient    The mail address of the recipient
   * @param subject      The subject of the mail
   * @param htmlTemplate The name of the html template
   */
  public void prepareAndSendHtmlMail(String recipient, String subject, String htmlTemplate,
      List<TemplateImage> templateImages) throws SmtpMailServiceException {

    if (mailSender == null) {
      throw new SmtpMailServiceException("No sender mail address set");
    }

    try {
      javaMailSender
          .send(buildHtmlMessagePreparator(recipient, subject, htmlTemplate, templateImages));
    } catch (MailException ex) {
      throw new SmtpMailServiceException("Mail could not be send", ex);
    }

  }

  private MimeMessagePreparator buildHtmlMessagePreparator(String recipient, String subject,
      String htmlTemplate, List<TemplateImage> templateImages) {
    return mimeMessage -> {
      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage,
          (!CollectionUtils.isEmpty(templateImages)), "UTF-8");
      messageHelper.setFrom(this.mailSender);
      if (isNotBlank(fixMailRecipient)) {
        messageHelper.setTo(fixMailRecipient);
      } else {
        messageHelper.setTo(recipient);
      }
      messageHelper.setSubject(subject);
      messageHelper.setText(htmlTemplate, true);

      if (!CollectionUtils.isEmpty(templateImages)) {
        for (TemplateImage templateImage : templateImages) {
          InputStreamSource inputStreamSource = newResources ? new ByteArrayResource(
              IOUtils
                  .toByteArray(new FileInputStream(resourcePath + NEW_TEMPLATE_IMAGE_DIR + templateImage
                      .getFilename()))) : new ClassPathResource(
              TEMPLATE_IMAGE_DIR + templateImage
                  .getFilename());
          messageHelper.addInline(templateImage.getFilename(), inputStreamSource,
              templateImage.getFiletype());
        }
      }
    };
  }

  /**
   * Preparing and sending an simple text mail.
   *
   * @param recipient The mail address of the recipient
   * @param subject   The subject of the mail
   * @param body      The body of the mail
   */
  public void prepareAndSendTextMail(String recipient, String subject, String body)
      throws SmtpMailServiceException {

    if (mailSender == null) {
      throw new SmtpMailServiceException("No sender mail address set");
    }

    try {
      javaMailSender.send(buildTextMessagePreparator(recipient, subject, body));
    } catch (MailException ex) {
      throw new SmtpMailServiceException("Mail could not be send", ex);
    }
  }

  private MimeMessagePreparator buildTextMessagePreparator(String recipient, String subject,
      String body) {
    return mimeMessage -> {

      MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
      messageHelper.setFrom(this.mailSender);
      if (isNotBlank(fixMailRecipient)) {
        messageHelper.setTo(fixMailRecipient);
      } else {
        String[] recipients = recipient.split(",");
        messageHelper.setTo(recipients);
      }
      messageHelper.setSubject(subject);
      messageHelper.setText(body, false);
    };
  }

}

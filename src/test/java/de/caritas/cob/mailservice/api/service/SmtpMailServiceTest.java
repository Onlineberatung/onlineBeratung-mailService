package de.caritas.cob.mailservice.api.service;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import de.caritas.cob.mailservice.api.exception.ServiceException;

@RunWith(MockitoJUnitRunner.class)
public class SmtpMailServiceTest {

  public final String SENDER = "name@domain.de";
  public final String RECIPIENT = "name@domain.de";
  public final String RECIPIENTS = "name@domain.de,name2@domain.de";
  public final String SUBJECT = "subject";
  public final String TEMPLATE = "test";
  public final String BODY = "test";

  @Mock
  private JavaMailSender javaMailSender;

  private SmtpMailService mailService;

  @Before
  public void setup() {
    this.mailService = new SmtpMailService(javaMailSender);
  }

  @Test
  public void prepareAndSendHtmlMail_Should_ThrowServiceException_WhenSenderMailAddressIsNotSet() {

    try {
      mailService.prepareAndSendHtmlMail(RECIPIENT, SUBJECT, TEMPLATE, null);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
    }

  }

  @Test
  public void prepareAndSendHtmlMail_Should_ThrowServiceException_WhenMailCouldNotBeSend()
      throws NoSuchFieldException, SecurityException {

    FieldSetter.setField(mailService, mailService.getClass().getDeclaredField("mailSender"),
        String.valueOf(SENDER));

    @SuppressWarnings("serial")
    MailException mailException = new MailException("reason") {};
    doThrow(mailException).when(javaMailSender).send(Mockito.any(MimeMessagePreparator.class));

    try {
      mailService.prepareAndSendHtmlMail(RECIPIENT, SUBJECT, TEMPLATE, null);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
    }

  }

  @Test
  public void prepareAndSendTextMail_Should_ThrowServiceException_WhenSenderMailAddressIsNotSet() {

    try {
      mailService.prepareAndSendTextMail(RECIPIENT, SUBJECT, BODY);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
    }

  }

  @Test
  public void prepareAndSendTextMail_Should_ThrowServiceException_WhenMailCouldNotBeSend()
      throws NoSuchFieldException, SecurityException {

    FieldSetter.setField(mailService, mailService.getClass().getDeclaredField("mailSender"),
        String.valueOf(SENDER));

    @SuppressWarnings("serial")
    MailException mailException = new MailException("reason") {};
    doThrow(mailException).when(javaMailSender).send(Mockito.any(MimeMessagePreparator.class));

    try {
      mailService.prepareAndSendTextMail(RECIPIENT, SUBJECT, BODY);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
    }

  }


}

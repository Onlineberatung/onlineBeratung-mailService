package de.caritas.cob.MailService.api.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;
import de.caritas.cob.MailService.api.exception.ServiceException;

@RunWith(MockitoJUnitRunner.class)
public class ExchangeMailServiceTest {

  public final String SENDER = "name@domain.de";
  public final String RECIPIENT = "name@domain.de";
  public final String SUBJECT = "subject";
  public final String TEMPLATE = "test";
  public final String BODY = "test";

  public final String EXCHANGE_USER_FIELD_NAME = "exchangeUser";
  public final String EXCHANGE_PASSWORD_FIELD_NAME = "exchangePassword";
  public final String EXCHANGE_URL_FIELD_NAME = "exchangeUrl";
  public final String EXCHANGE_VERSION_FIELD_NAME = "exchangeVersion";
  public final String EXCHANGE_USER_VALUE = "dummyUser";
  public final String EXCHANGE_PASSWORD_VALUE = "dummyPassword";
  public final String EXCHANGE_URL_VALUE = "dummyULR";
  public final String EXCHANGE_VERSION_VALUE = "Exchange2007_SP1";

  @Mock
  private LogService logService;

  private ExchangeMailService mailService;


  @Before
  public void setup() throws Exception, SecurityException {
    this.mailService = new ExchangeMailService(logService);
    FieldSetter.setField(this.mailService,
        this.mailService.getClass().getDeclaredField(EXCHANGE_USER_FIELD_NAME),
        EXCHANGE_USER_VALUE);
    FieldSetter.setField(this.mailService,
        this.mailService.getClass().getDeclaredField(EXCHANGE_PASSWORD_FIELD_NAME),
        EXCHANGE_PASSWORD_VALUE);
    FieldSetter.setField(this.mailService,
        this.mailService.getClass().getDeclaredField(EXCHANGE_URL_FIELD_NAME), EXCHANGE_URL_VALUE);
    FieldSetter.setField(this.mailService,
        this.mailService.getClass().getDeclaredField(EXCHANGE_VERSION_FIELD_NAME),
        EXCHANGE_VERSION_VALUE);
  }

  @Test
  public void prepareAndSendHtmlMail_Should_ThrowServiceException_WhenSenderMailAddressIsNotSet() {
    try {
      mailService.prepareAndSendHtmlMail(RECIPIENT, SUBJECT, TEMPLATE, null);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
      assertEquals("No sender mail address set", serviceException.getMessage());
    }
  }

  @Test
  public void prepareAndSendHtmlMail_Should_ThrowServiceException_WhenMailCouldNotBeSend()
      throws NoSuchFieldException, SecurityException {

    FieldSetter.setField(mailService, mailService.getClass().getDeclaredField("mailSender"),
        String.valueOf(SENDER));

    try {
      mailService.prepareAndSendHtmlMail(RECIPIENT, SUBJECT, TEMPLATE, null);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
      assertEquals("Error while sending email", serviceException.getMessage());
    }
  }

  @Test
  public void prepareAndSendTextMail_Should_ThrowServiceException_WhenSenderMailAddressIsNotSet() {
    try {
      mailService.prepareAndSendTextMail(RECIPIENT, SUBJECT, BODY);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
      assertEquals("No sender mail address set", serviceException.getMessage());
    }
  }

  @Test
  public void prepareAndSendTextMail_Should_ThrowServiceException_WhenMailCouldNotBeSend()
      throws NoSuchFieldException, SecurityException {

    FieldSetter.setField(mailService, mailService.getClass().getDeclaredField("mailSender"),
        String.valueOf(SENDER));

    try {
      mailService.prepareAndSendTextMail(RECIPIENT, SUBJECT, BODY);
      fail("Expected exception: ServiceException");
    } catch (ServiceException serviceException) {
      assertTrue("Excepted ServiceException thrown", true);
      assertEquals("Error while sending email", serviceException.getMessage());
    }
  }

}

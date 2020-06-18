package de.caritas.cob.MailService.api.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.io.PrintWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.FieldSetter;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class LogServiceTest {

  private final String ERROR_MESSAGE = "error message";
  private final String ERROR_RECIPIENS = "name@domain.de";
  private final String FIELD_NAME_USE_SMTP = "useSMTP";

  @Mock
  private SmtpMailService mailService;

  @Mock
  ExchangeMailService exchangeMailService;

  @InjectMocks
  private LogService logService;

  @Test
  public void logError_Should_LogExceptionStackTrace() {

    Exception exception = Mockito.mock(Exception.class);
    logService.logError(ERROR_MESSAGE, exception);
    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  @Test
  public void logError_Should_SendErrorMail_WhenErrorRecipientIsSetAndUsingExchange()
      throws NoSuchFieldException, SecurityException {

    FieldSetter.setField(logService, logService.getClass().getDeclaredField("errorRecipients"),
        String.valueOf(ERROR_RECIPIENS));
    FieldSetter.setField(logService, logService.getClass().getDeclaredField(FIELD_NAME_USE_SMTP),
        false);

    Exception exception = Mockito.mock(Exception.class);
    logService.logError(ERROR_MESSAGE, exception);
    verify(exchangeMailService, atLeastOnce()).prepareAndSendTextMail(Mockito.eq(ERROR_RECIPIENS),
        Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void logError_Should_SendErrorMail_WhenErrorRecipientIsSetAndUsingSmtp()
      throws NoSuchFieldException, SecurityException {

    FieldSetter.setField(logService, logService.getClass().getDeclaredField("errorRecipients"),
        String.valueOf(ERROR_RECIPIENS));
    FieldSetter.setField(logService, logService.getClass().getDeclaredField(FIELD_NAME_USE_SMTP),
        true);

    Exception exception = Mockito.mock(Exception.class);
    logService.logError(ERROR_MESSAGE, exception);
    verify(mailService, atLeastOnce()).prepareAndSendTextMail(Mockito.eq(ERROR_RECIPIENS),
        Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void logError_ShouldNot_SendErrorMail_WhenErrorRecipientIsNotSet()
      throws NoSuchFieldException, SecurityException {

    Exception exception = Mockito.mock(Exception.class);
    logService.logError(ERROR_MESSAGE, exception);
    verify(mailService, times(0)).prepareAndSendTextMail(Mockito.anyString(), Mockito.anyString(),
        Mockito.anyString());
  }

}

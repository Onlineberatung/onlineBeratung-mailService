package de.caritas.cob.mailservice.api.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import de.caritas.cob.mailservice.api.exception.ExchangeMailServiceException;
import de.caritas.cob.mailservice.api.exception.SmtpMailServiceException;
import de.caritas.cob.mailservice.api.exception.TemplateDescriptionServiceException;
import de.caritas.cob.mailservice.api.exception.TemplateServiceException;
import de.caritas.cob.mailservice.api.helper.TemplateDataConverter;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import de.caritas.cob.mailservice.api.model.MailDTO;
import de.caritas.cob.mailservice.api.model.MailsDTO;
import de.caritas.cob.mailservice.api.model.TemplateDataDTO;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class MailServiceTest {

  private static final String EMAIL = "email";
  private final String ERROR_MESSAGE = "error message";
  private final String ERROR_RECIPIENS = "name@domain.de";
  private final String FIELD_NAME_USE_SMTP = "useSmtp";

  @InjectMocks
  private MailService mailService;

  @Mock
  private SmtpMailService smtpMailService;

  @Mock
  private ExchangeMailService exchangeMailService;

  @Mock
  private TemplateDescriptionService templateDescriptionService;

  @Mock
  private TemplateService templateService;

  @Mock
  private TemplateDataConverter templateDataConverter;

  @Test
  public void sendErrorMail_Should_SendErrorMail_WhenErrorRecipientIsSetAndUsingExchange()
      throws SecurityException, ExchangeMailServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, false);

    mailService.sendErrorMail(ERROR_MESSAGE);
    verify(exchangeMailService, atLeastOnce()).prepareAndSendTextMail(eq(ERROR_RECIPIENS),
        Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void sendErrorMail_Should_SendErrorMail_WhenErrorRecipientIsSetAndUsingSmtp()
      throws SecurityException, SmtpMailServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, true);

    mailService.sendErrorMail(ERROR_MESSAGE);
    verify(smtpMailService, atLeastOnce()).prepareAndSendTextMail(eq(ERROR_RECIPIENS),
        Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void sendErrorMail_ShouldNot_SendErrorMail_WhenErrorRecipientIsNotSet()
      throws SecurityException, SmtpMailServiceException {

    mailService.sendErrorMail(ERROR_MESSAGE);
    verify(smtpMailService, times(0))
        .prepareAndSendTextMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void sendHtmlMails_Should_sendHtmlMails_WhenMailHasAdrressAndUsingExchange()
      throws SecurityException, ExchangeMailServiceException, TemplateDescriptionServiceException, TemplateServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, false);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenReturn(Optional.of(new TemplateDescription()));
    when(templateService.getProcessedHtmlTemplate(any(), any(), any()))
        .thenReturn(Optional.of("success"));

    MailsDTO mailsDTO = new MailsDTO().mails(singletonList(createMailDTO()));

    mailService.sendHtmlMails(mailsDTO);
    verify(exchangeMailService, atLeastOnce()).prepareAndSendHtmlMail(eq(EMAIL),
        any(), eq("success"), any());
  }

  private MailDTO createMailDTO() {
    return new MailDTO().template("template").email(EMAIL).templateData(
        singletonList(new TemplateDataDTO().key("key").value("value")));
  }

  @Test
  public void sendHtmlMails_Should_sendHtmlMails_WhenUsingSmtp()
      throws SecurityException, SmtpMailServiceException, TemplateDescriptionServiceException, TemplateServiceException {
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, true);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenReturn(Optional.of(new TemplateDescription()));
    when(templateService.getProcessedHtmlTemplate(any(), any(), any()))
        .thenReturn(Optional.of("success"));

    MailsDTO mailsDTO = new MailsDTO().mails(singletonList(createMailDTO()));

    mailService.sendHtmlMails(mailsDTO);
    verify(smtpMailService, atLeastOnce()).prepareAndSendHtmlMail(eq(EMAIL),
        any(), eq("success"), any());
  }

  @Test
  public void sendHtmlMails_ShouldNot_sendHtmlMails_WhenNoMailServiceIsSet()
      throws SecurityException {
    MailsDTO mailsDTO = new MailsDTO().mails(singletonList(createMailDTO()));

    mailService.sendHtmlMails(mailsDTO);
    verifyZeroInteractions(smtpMailService);
    verifyZeroInteractions(exchangeMailService);
  }

  @Test
  public void sendHtmlMails_Should_sendSeveralHtmlMails_WhenMoreThanOneMailIsInMailsDTO()
      throws SecurityException, SmtpMailServiceException, TemplateDescriptionServiceException, TemplateServiceException {
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, true);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenReturn(Optional.of(new TemplateDescription()));
    when(templateService.getProcessedHtmlTemplate(any(), any(), any()))
        .thenReturn(Optional.of("success"));

    MailsDTO mailsDTO = new MailsDTO().mails(asList(createMailDTO(), createMailDTO(), createMailDTO()));

    mailService.sendHtmlMails(mailsDTO);
    verify(smtpMailService, times(3)).prepareAndSendHtmlMail(eq(EMAIL),
        any(), eq("success"), any());
  }

  @Test
  public void sendHtmlMails_Should_sendErrorMail_WhenTemplateDescriptionServiceExceptionIsThrown()
      throws SecurityException, SmtpMailServiceException, TemplateDescriptionServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, true);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenThrow(new TemplateDescriptionServiceException("", new Exception()));

    MailsDTO mailsDTO = new MailsDTO().mails(singletonList(createMailDTO()));
    mailService.sendHtmlMails(mailsDTO);
    verify(smtpMailService, atLeastOnce()).prepareAndSendTextMail(eq(ERROR_RECIPIENS),
        Mockito.anyString(), Mockito.anyString());
  }

}

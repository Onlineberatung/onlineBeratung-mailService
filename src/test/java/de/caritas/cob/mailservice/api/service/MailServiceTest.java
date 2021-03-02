package de.caritas.cob.mailservice.api.service;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

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
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
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
  private final String ERROR_RECIPIENTS = "name@domain.de";
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
  public void sendErrorMail_Should_SendErrorMail_When_ErrorRecipientIsSetAndUsingExchange()
      throws SecurityException, ExchangeMailServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENTS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, false);

    mailService.sendErrorMail(ERROR_MESSAGE);
    verify(exchangeMailService, atLeastOnce()).prepareAndSendTextMail(eq(ERROR_RECIPIENTS),
        Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void sendErrorMail_Should_SendErrorMail_When_ErrorRecipientIsSetAndUsingSmtp()
      throws SecurityException, SmtpMailServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENTS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, true);

    mailService.sendErrorMail(ERROR_MESSAGE);
    verify(smtpMailService, atLeastOnce()).prepareAndSendTextMail(eq(ERROR_RECIPIENTS),
        Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void sendErrorMail_ShouldNot_SendErrorMail_When_ErrorRecipientIsNotSet()
      throws SecurityException, SmtpMailServiceException {

    mailService.sendErrorMail(ERROR_MESSAGE);
    verify(smtpMailService, times(0))
        .prepareAndSendTextMail(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
  }

  @Test
  public void sendHtmlMails_Should_sendHtmlMails_When_MailHasAddressAndUsingExchange()
      throws SecurityException, ExchangeMailServiceException, TemplateDescriptionServiceException, TemplateServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENTS);
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
  public void sendHtmlMails_Should_sendHtmlMails_When_UsingSmtp()
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
  public void sendHtmlMails_ShouldNot_sendHtmlMails_When_NoMailServiceIsSet()
      throws SecurityException {
    MailsDTO mailsDTO = new MailsDTO().mails(singletonList(createMailDTO()));

    mailService.sendHtmlMails(mailsDTO);
    verifyNoMoreInteractions(smtpMailService);
    verifyNoMoreInteractions(exchangeMailService);
  }

  @Test
  public void sendHtmlMails_Should_sendSeveralHtmlMails_When_MoreThanOneMailIsInMailsDTO()
      throws SecurityException, SmtpMailServiceException, TemplateDescriptionServiceException, TemplateServiceException {
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, true);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenReturn(Optional.of(new TemplateDescription()));
    when(templateService.getProcessedHtmlTemplate(any(), any(), any()))
        .thenReturn(Optional.of("success"));

    MailsDTO mailsDTO = new MailsDTO()
        .mails(asList(createMailDTO(), createMailDTO(), createMailDTO()));

    mailService.sendHtmlMails(mailsDTO);
    verify(smtpMailService, times(3)).prepareAndSendHtmlMail(eq(EMAIL),
        any(), eq("success"), any());
  }

  @Test
  public void sendHtmlMails_Should_sendErrorMail_When_TemplateDescriptionServiceExceptionIsThrown()
      throws SecurityException, SmtpMailServiceException, TemplateDescriptionServiceException {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENTS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, true);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenThrow(new TemplateDescriptionServiceException("", new Exception()));

    MailsDTO mailsDTO = new MailsDTO().mails(singletonList(createMailDTO()));
    mailService.sendHtmlMails(mailsDTO);
    verify(smtpMailService, atLeastOnce()).prepareAndSendTextMail(eq(ERROR_RECIPIENTS),
        Mockito.anyString(), Mockito.anyString());
  }

  @Test(expected = InternalServerErrorException.class)
  public void sendHtmlMail_Should_ThrowInternalServerErrorExceptionAndLogExceptionStackTrace_When_AnErrorOccursDuringSendingMails()
      throws ExchangeMailServiceException, TemplateDescriptionServiceException, TemplateServiceException {
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, false);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenReturn(Optional.of(new TemplateDescription()));
    when(templateService.getProcessedHtmlTemplate(any(), any(), any()))
        .thenReturn(Optional.of("success"));
    ExchangeMailServiceException exception = mock(ExchangeMailServiceException.class);
    doThrow(exception).when(exchangeMailService)
        .prepareAndSendHtmlMail(any(), any(), any(), any());

    MailsDTO mailsDTO = new MailsDTO().mails(singletonList(createMailDTO()));
    mailService.sendHtmlMails(mailsDTO);

    verify(exception, atLeastOnce()).printStackTrace(any(PrintWriter.class));
  }

  @Test
  public void sendErrotMailDto_Should_sendUnescapedHtmlErrorMail() throws Exception {
    ReflectionTestUtils.setField(mailService, "errorRecipients", ERROR_RECIPIENTS);
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, false);
    TemplateDescription templateDescription = new TemplateDescription();
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenReturn(Optional.of(templateDescription));
    when(templateService.getProcessedHtmlTemplate(any(), any(), any()))
        .thenReturn(Optional.of("success"));
    ErrorMailDTO errorMailDTO = new ErrorMailDTO()
        .template("free-text")
        .templateData(singletonList(
            new TemplateDataDTO().key("text").value("<h2>test</h2>")
        ));

    this.mailService.sendErrorMailDto(errorMailDTO);

    Map<String, Object> expectedData = new HashMap<>();
    expectedData.put("text", "<h2>test</h2>");
    verify(this.exchangeMailService, times(1)).prepareAndSendHtmlMail(any(), any(), any(), any());
    verify(this.templateService, times(1)).getProcessedSubject(eq(templateDescription),
        eq(expectedData));
  }

  @Test(expected = InternalServerErrorException.class)
  public void sendErrotMailDto_Should_ThrowInternalServerErrorExceptionAndLogExceptionStackTrace_When_AnErrorOccursDuringSendingMails()
      throws Exception {
    ReflectionTestUtils.setField(mailService, FIELD_NAME_USE_SMTP, false);
    when(templateDescriptionService.getTemplateDescription(any()))
        .thenReturn(Optional.of(new TemplateDescription()));
    when(templateService.getProcessedHtmlTemplate(any(), any(), any()))
        .thenThrow(new TemplateServiceException(""));
    ErrorMailDTO errorMailDTO = new ErrorMailDTO()
        .template("free-text")
        .templateData(singletonList(
            new TemplateDataDTO().key("text").value("<h2>test</h2>")
        ));

    mailService.sendErrorMailDto(errorMailDTO);
  }

}

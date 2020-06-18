package de.caritas.cob.MailService.api.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.Optional;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import de.caritas.cob.MailService.api.helper.Helper;
import de.caritas.cob.MailService.api.helper.TemplateDataConverter;
import de.caritas.cob.MailService.api.mailTemplate.TemplateDescription;
import de.caritas.cob.MailService.api.service.ExchangeMailService;
import de.caritas.cob.MailService.api.service.LogService;
import de.caritas.cob.MailService.api.service.SmtpMailService;
import de.caritas.cob.MailService.api.service.TemplateDescriptionService;
import de.caritas.cob.MailService.api.service.TemplateService;

@RunWith(SpringRunner.class)
@WebMvcTest(MailController.class)
@AutoConfigureMockMvc(secure = false)
@TestPropertySource(properties = {"mail.usesmtp=false"})
public class MailControllerTestExchangeIT {

  private final String PATH_SEND_MAIL = "/mails/send";
  private final String TEMPLATE = "test";
  private final String HTML_TEMPLATE = "<html></html>";
  private final String VALID_REQUEST_BODY =
      "{\"mails\":[{" + "\"template\":\"" + TEMPLATE + "\",\"email\":\"dah@o4b.de\","
          + "\"templateData\":[" + "{\"key\":\"name\"," + "\"value\":\"Max Mustermann\"},"
          + "{\"key\":\"text\"," + "\"value\":\"hello, world!\"}" + "]}]}";
  private final TemplateDescription TEMPLATE_DESCRIPTION =
      new TemplateDescription("test.html", "subject", null, null);

  @Autowired
  private MockMvc mvc;

  @MockBean
  private LogService logService;

  @MockBean
  private TemplateDescriptionService templateDescriptionService;

  @MockBean
  private TemplateService templateService;

  @MockBean
  private TemplateDataConverter templateDataConverter;

  @MockBean
  ExchangeMailService exchangeMailService;

  @MockBean
  SmtpMailService smtpMailService;

  @MockBean
  private Helper helper;

  @Test
  public void sendMail_Should_ReturnOk_WhenTemplateDescriptionIsNotFound() throws Exception {

    mvc.perform(post(PATH_SEND_MAIL).content(VALID_REQUEST_BODY)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

  }

  @Test
  public void sendMail_Should_SendHtmlMail_And_ReturnOk_WhenExchange() throws Exception {

    when(templateDescriptionService.getTemplateDescription(TEMPLATE))
        .thenReturn(Optional.of(TEMPLATE_DESCRIPTION));
    when(templateService.getProcessedHtmlTemplate(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Optional.of(HTML_TEMPLATE));

    mvc.perform(post(PATH_SEND_MAIL).content(VALID_REQUEST_BODY)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(exchangeMailService, times(1)).prepareAndSendHtmlMail(Mockito.any(), Mockito.any(),
        Mockito.any(), Mockito.any());

  }



}

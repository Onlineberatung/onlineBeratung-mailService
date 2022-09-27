package de.caritas.cob.mailservice.api.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.caritas.cob.mailservice.api.service.MailService;
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

@RunWith(SpringRunner.class)
@WebMvcTest(MailController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {"mail.usesmtp=false"})
public class MailControllerTestExchangeIT {

  private final String PATH_SEND_MAIL = "/mails/send";
  private final String PATH_SEND_ERROR_MAIL = "/mails/error/send";
  private final String TEMPLATE = "test";
  private final String VALID_REQUEST_BODY =
      "{\"mails\":[{" + "\"template\":\"" + TEMPLATE + "\",\"email\":\"dah@o4b.de\","
          + "\"templateData\":[" + "{\"key\":\"name\"," + "\"value\":\"Max Mustermann\"},"
          + "{\"key\":\"text\"," + "\"value\":\"hello, world!\"}" + "]}]}";
  private final String VALID_ERROR_REQUEST_BODY =
      "{" + "\"template\":\"" + TEMPLATE + "\",\"email\":\"dah@o4b.de\","
          + "\"templateData\":[" + "{\"key\":\"name\"," + "\"value\":\"Max Mustermann\"},"
          + "{\"key\":\"text\"," + "\"value\":\"hello, world!\"}" + "]}";

  @Autowired
  private MockMvc mvc;

  @MockBean
  MailService mailService;

  @Test
  public void sendMail_Should_ReturnOk_WhenTemplateDescriptionIsNotFound() throws Exception {

    mvc.perform(post(PATH_SEND_MAIL).content(VALID_REQUEST_BODY)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

  }

  @Test
  public void sendMail_Should_SendHtmlMail_And_ReturnOk_WhenExchange() throws Exception {

    mvc.perform(post(PATH_SEND_MAIL).content(VALID_REQUEST_BODY)
        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(mailService, times(1)).sendHtmlMails(Mockito.any());

  }

  @Test
  public void sendErrorMail_Should_ReturnOk_WhenTemplateDescriptionIsNotFound() throws Exception {

    mvc.perform(post(PATH_SEND_ERROR_MAIL)
        .content(VALID_ERROR_REQUEST_BODY)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

  }

  @Test
  public void sendErrorMail_Should_SendHtmlMail_And_ReturnOk_WhenExchange() throws Exception {

    mvc.perform(post(PATH_SEND_ERROR_MAIL)
        .content(VALID_ERROR_REQUEST_BODY)
        .contentType(MediaType.APPLICATION_JSON)
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());

    verify(mailService, times(1)).sendErrorMailDto(Mockito.any());

  }

}

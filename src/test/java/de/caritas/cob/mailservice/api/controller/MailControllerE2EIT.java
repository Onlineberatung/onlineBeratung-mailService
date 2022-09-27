package de.caritas.cob.mailservice.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.mailservice.api.model.LanguageCode;
import de.caritas.cob.mailservice.api.model.MailDTO;
import de.caritas.cob.mailservice.api.model.MailsDTO;
import de.caritas.cob.mailservice.api.model.TemplateDataDTO;
import java.util.List;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class MailControllerE2EIT {

  private static final String CSRF_HEADER = "csrfHeader";
  private static final String CSRF_VALUE = "test";
  private static final Cookie CSRF_COOKIE = new Cookie("csrfCookie", CSRF_VALUE);

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private JavaMailSender javaMailSender;

  @Captor
  private ArgumentCaptor<MimeMessagePreparator> mimeMessagePrepCaptor;

  private MailsDTO mailsDTO;
  private Map<String, List<Map<String, Object>>> mailsDTOMap;

  @Test
  void sendMailsShouldRespondWithOkWhenEmailListIsEmpty() throws Exception {
    givenAnEmptyEmailList();

    mockMvc.perform(
        post("/mails/send")
            .cookie(CSRF_COOKIE)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mailsDTO))
            .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());
  }

  @Test
  void sendMailsShouldSendEmailAndRenderDataWithDefaultLanguageWhenLanguageNotGiven()
      throws Exception {
    givenAnEmailListWithoutLanguage();

    mockMvc.perform(
        post("/mails/send")
            .cookie(CSRF_COOKIE)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mailsDTOMap))
            .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    verify(javaMailSender).send(mimeMessagePrepCaptor.capture());

    var prep = mimeMessagePrepCaptor.getValue();
    var mailDTO = mailsDTO.getMails().get(0);

    var recipient = getArg(prep, 3);
    assertEquals(mailDTO.getEmail(), recipient);

    var subject = getArg(prep, 4);
    assertEquals("Neuzuweisung erfolgt", subject);

    assertTextIsHtml(prep);
    assertTextIsGerman(prep, mailDTO);
  }

  @Test
  void sendMailsShouldSendEmailAndRenderDataWithDefaultLanguageWhenLanguageIsNull()
      throws Exception {
    givenAnEmailList(null);

    mockMvc.perform(
        post("/mails/send")
            .cookie(CSRF_COOKIE)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mailsDTO))
            .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    verify(javaMailSender).send(mimeMessagePrepCaptor.capture());

    var prep = mimeMessagePrepCaptor.getValue();
    var mailDTO = mailsDTO.getMails().get(0);

    var recipient = getArg(prep, 3);
    assertEquals(mailDTO.getEmail(), recipient);

    var subject = getArg(prep, 4);
    assertEquals("Neuzuweisung erfolgt", subject);

    assertTextIsHtml(prep);
    assertTextIsGerman(prep, mailDTO);
  }

  @Test
  void sendMailsShouldSendEmailAndRenderDataWithDefaultLanguageWhenLanguageDoesNotExist()
      throws Exception {
    givenAnEmailList(LanguageCode.IO);

    mockMvc.perform(
        post("/mails/send")
            .cookie(CSRF_COOKIE)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mailsDTO))
            .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    verify(javaMailSender).send(mimeMessagePrepCaptor.capture());

    var prep = mimeMessagePrepCaptor.getValue();
    var mailDTO = mailsDTO.getMails().get(0);

    var recipient = getArg(prep, 3);
    assertEquals(mailDTO.getEmail(), recipient);

    var subject = getArg(prep, 4);
    assertEquals("Neuzuweisung erfolgt", subject);

    assertTextIsHtml(prep);
    assertTextIsGerman(prep, mailDTO);
  }

  @Test
  void sendMailsShouldSendEmailAndRenderDataWhenLanguageIsDefaultLanguage() throws Exception {
    givenAnEmailList(LanguageCode.DE);

    mockMvc.perform(
        post("/mails/send")
            .cookie(CSRF_COOKIE)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mailsDTO))
            .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    verify(javaMailSender).send(mimeMessagePrepCaptor.capture());

    var prep = mimeMessagePrepCaptor.getValue();
    var mailDTO = mailsDTO.getMails().get(0);

    var recipient = getArg(prep, 3);
    assertEquals(mailDTO.getEmail(), recipient);

    var subject = getArg(prep, 4);
    assertEquals("Neuzuweisung erfolgt", subject);

    assertTextIsHtml(prep);
    assertTextIsGerman(prep, mailDTO);
  }

  @Test
  void sendMailsShouldSendEmailAndRenderDataWithSetLanguage() throws Exception {
    givenAnEmailList(LanguageCode.EN);

    mockMvc.perform(
        post("/mails/send")
            .cookie(CSRF_COOKIE)
            .header(CSRF_HEADER, CSRF_VALUE)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(mailsDTO))
            .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk());

    verify(javaMailSender).send(mimeMessagePrepCaptor.capture());

    var prep = mimeMessagePrepCaptor.getValue();
    var mailDTO = mailsDTO.getMails().get(0);

    var recipient = getArg(prep, 3);
    assertEquals(mailDTO.getEmail(), recipient);

    var subject = getArg(prep, 4);
    assertEquals("Reassignment Done", subject);

    assertTextIsHtml(prep);
    assertTextIsEnglish(prep, mailDTO);
  }

  private void givenAnEmptyEmailList() {
    mailsDTO = new MailsDTO().mails(List.of());
  }

  private void givenAnEmailList(LanguageCode languageCode) {
    var email = new MailDTO();
    email.setEmail(RandomStringUtils.randomAlphanumeric(32));
    email.setTemplate("reassign-confirmation-notification");

    var nameRecipient = new TemplateDataDTO()
        .key("name_recipient")
        .value(RandomStringUtils.randomAlphanumeric(16));
    var nameFromConsultant = new TemplateDataDTO()
        .key("name_from_consultant")
        .value(RandomStringUtils.randomAlphanumeric(16));
    var url = new TemplateDataDTO()
        .key("url")
        .value(RandomStringUtils.randomAlphanumeric(16));
    email.setTemplateData(List.of(nameRecipient, nameFromConsultant, url));
    email.setLanguage(languageCode);

    mailsDTO = new MailsDTO().mails(List.of(email));
  }

  @SuppressWarnings("unchecked")
  private void givenAnEmailListWithoutLanguage() {
    givenAnEmailList(null);
    mailsDTOMap = objectMapper.convertValue(this.mailsDTO, Map.class);
    mailsDTOMap.get("mails").get(0).remove("language");
  }

  private String valueOf(String key, List<TemplateDataDTO> templateDataDTOList) {
    return templateDataDTOList.stream()
        .filter(templateDataDTO -> templateDataDTO.getKey().equals(key))
        .findFirst()
        .map(TemplateDataDTO::getValue)
        .orElseThrow();
  }

  private static String getArg(MimeMessagePreparator prep, int n)
      throws NoSuchFieldException, IllegalAccessException {
    var recipientField = prep.getClass().getDeclaredField("arg$" + n);
    recipientField.setAccessible(true);

    return recipientField.get(prep).toString();
  }

  private void assertTextIsHtml(MimeMessagePreparator prep)
      throws NoSuchFieldException, IllegalAccessException {
    var text = getArg(prep, 5).trim();
    assertTrue(text.startsWith("<!DOCTYPE html>"));
    assertTrue(text.endsWith("</html>"));
  }

  private void assertTextIsGerman(MimeMessagePreparator prep, MailDTO mailDTO)
      throws NoSuchFieldException, IllegalAccessException {
    var text = getArg(prep, 5);
    var data = mailDTO.getTemplateData();
    var salutation = "<b>Liebe(r) <span>" + valueOf("name_recipient", data) + "</span>,</b>";
    assertTrue(text.contains(salutation));

    var message = "<span>"
        + valueOf("name_from_consultant", data)
        + "</span> hat Ihnen eine_n Ratsuchende_n übergeben.";
    assertTrue(text.contains(message));

    var anchorStart = "<a href=\"" + valueOf("url", data) + "\">";
    assertTrue(text.contains(anchorStart));
  }

  private void assertTextIsEnglish(MimeMessagePreparator prep, MailDTO mailDTO)
      throws NoSuchFieldException, IllegalAccessException {
    var text = getArg(prep, 5);
    var data = mailDTO.getTemplateData();
    var salutation = "<strong>Dear <span>" + valueOf("name_recipient", data) + "</span>,</strong>";
    assertTrue(text.contains(salutation));

    var message = "<span>"
        + valueOf("name_from_consultant", data)
        + "</span> has assigned you an advice seeker.";
    assertTrue(text.contains(message));

    var anchorStart = "<a href=\"" + valueOf("url", data) + "\">";
    assertTrue(text.contains(anchorStart));
  }
}

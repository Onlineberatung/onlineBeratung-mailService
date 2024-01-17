package de.caritas.cob.mailservice.api.service;



import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.google.common.collect.Lists;
import de.caritas.cob.mailservice.api.exception.TemplateServiceException;
import de.caritas.cob.mailservice.api.mailtemplate.SubjectDescription;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.api.model.LanguageCode;
import de.caritas.cob.mailservice.api.model.MailDTO;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("testing")
class TemplateServiceIntegrationTest {

  @Autowired
  private TemplateService templateService;

  @Autowired
  private TranslationService translationService;

  @Test
  void getProcessedHtmlTemplate_Should_RenderTemplateData_With_InformalDialect()
      throws TemplateServiceException {

    Optional<String> renderedHtml = templateService.render(
        new TemplateDescription(Map.of(LanguageCode.DE, "message-notification-consultant.html"),
            new SubjectDescription("translationKey"),
            Lists.newArrayList(),
            null),
        new MailDTO()
            .template("message-notification-consultant")
            .language(LanguageCode.DE)
            .dialect(Dialect.INFORMAL),
        new HashMap<>());
    assertThat(renderedHtml).isPresent();
    assertThat(renderedHtml.get()).contains("Du hast eine neue Nachricht in Deinen Beratungen");
  }

  @Test
  void getProcessedHtmlTemplate_Should_RenderTemplateData_With_FormalDialect()
      throws TemplateServiceException {

    Optional<String> renderedHtml = templateService.render(
        new TemplateDescription(Map.of(LanguageCode.DE, "message-notification-consultant.html"),
            new SubjectDescription("translationKey"),
            Lists.newArrayList(),
            null),
        new MailDTO()
            .template("message-notification-consultant")
            .language(LanguageCode.DE)
            .dialect(Dialect.FORMAL),
        new HashMap<>());
    assertThat(renderedHtml).isPresent();
    assertThat(renderedHtml.get()).contains("Sie haben eine neue Nachricht in Ihren Beratungen");
  }

}

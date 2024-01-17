package de.caritas.cob.mailservice.api.service;

import static de.caritas.cob.mailservice.api.helper.ThymeleafHelper.getProcessedHtml;

import de.caritas.cob.mailservice.api.exception.TemplateServiceException;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.api.model.LanguageCode;
import de.caritas.cob.mailservice.api.model.MailDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for template processing
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

  @Value("${app.imprint.url}")
  private String imprintUrl;

  @Value("${app.dataprivacy.url}")
  private String dataPrivacyUrl;

  @NonNull
  private final TranslationService translationService;

  /**
   * Get the processed html template with replaced placeholders
   *
   * @param desc     the template description
   * @param data     the template data
   * @return if success, an optional with the html template, otherwise an empty optional
   */
  public Optional<String> render(TemplateDescription desc, MailDTO mail, Map<String, Object> data) throws TemplateServiceException {

    data.put("urlimpressum", imprintUrl);
    data.put("urldatenschutz", dataPrivacyUrl);

    data.putAll(getTranslationMapAndDefaultToGermanIfNotFound(mail.getLanguage(), mail.getDialect()));
    List<String> missingFieldList = getMissingTemplateFields(desc, data);

    if (!CollectionUtils.isEmpty(missingFieldList)) {
      throw new TemplateServiceException(String.format(
          "Mail request for template %s could not be executed due to missing fields for template processing. Missing fields: %s",
          mail.getTemplate(), String.join(",", missingFieldList)));
    }

    var templateFilename = desc.getTemplateFilenameOrFallback(mail.getLanguage());

    return translationsArePresentAndNotEmpty(mail) ? getProcessedHtml(data, mail.getLanguage(), templateFilename, mail.getDialect()) :
        getProcessedHtml(data, LanguageCode.DE, templateFilename, mail.getDialect());
  }

  private boolean translationsArePresentAndNotEmpty(MailDTO mailDTO) {
    var translations = translationService.tryFetchTranslations(
        mailDTO.getLanguage().getValue(), mailDTO.getDialect());
    return translations.isPresent() && !translations.get().isEmpty();
  }

  private Map<String, String> getTranslationMapAndDefaultToGermanIfNotFound(LanguageCode language, Dialect dialect) {
    return translationService.tryFetchTranslations(language.getValue(), dialect).orElse(
        translationService.tryFetchTranslations(LanguageCode.DE.getValue(), dialect)
            .orElse(new HashMap<>()));
  }

  /**
   * Get the processed subject with replaced placeholders
   *
   * @param templateDescription the mail template
   * @param templateData        the template data
   * @return the subject with replaced placeholders
   */
  public String getRenderedSubject(TemplateDescription templateDescription,
      Map<String, Object> templateData, MailDTO mailDTO) {
    StringSubstitutor stringSubstitutor = new StringSubstitutor(templateData, "${", "}");
    var subjectKey = templateDescription.getSubject().getKey();
    if (subjectKey == null) {
      log.warn("Subject key is null for template {}", templateDescription.getHtmlTemplateFilename().get(LanguageCode.DE));
      return tryRenderDefaultSubject(stringSubstitutor);
    }
    return getRenderedSubjectForSubjectKeyNotNull(mailDTO, stringSubstitutor, subjectKey);
  }

  private String tryRenderDefaultSubject(StringSubstitutor stringSubstitutor) {
    return stringSubstitutor.replace("${subject}");
  }

  private String getRenderedSubjectForSubjectKeyNotNull(MailDTO mailDTO, StringSubstitutor stringSubstitutor,
      String subjectKey) {
    var translationKeys = getTranslationMapAndDefaultToGermanIfNotFound(mailDTO.getLanguage(),
        mailDTO.getDialect());
    String subject = translationKeys.get(subjectKey);
    if (subject != null) {
      return stringSubstitutor.replace(subject);
    } else {
      log.warn("Subject key {} not found in translations keys", subjectKey);
      return tryRenderDefaultSubject(stringSubstitutor);
    }
  }

  /**
   * Get the missing fields in the template data for a mail template
   *
   * @param mailTemplate The template description
   * @param templateData The template data
   * @return a list with the missing fields
   */
  private List<String> getMissingTemplateFields(TemplateDescription mailTemplate,
      Map<String, Object> templateData) {
    if (templateData == null) {
      templateData = new HashMap<>();
    }
    List<String> missingFieldList = new ArrayList<>();
    if (!CollectionUtils.isEmpty(mailTemplate.getTemplateDataFields())) {
      for (String fieldName : mailTemplate.getTemplateDataFields()) {
        if (!templateData.containsKey(fieldName)) {
          missingFieldList.add(fieldName);
        }
      }
    }
    return missingFieldList;
  }
}

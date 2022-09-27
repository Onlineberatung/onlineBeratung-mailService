package de.caritas.cob.mailservice.api.service;

import de.caritas.cob.mailservice.api.exception.TemplateServiceException;
import de.caritas.cob.mailservice.api.helper.ThymeleafHelper;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import de.caritas.cob.mailservice.api.model.LanguageCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * Service for template processing
 **/
@Service
public class TemplateService {

  @Value("${app.imprint.url}")
  private String imprintUrl;

  @Value("${app.dataprivacy.url}")
  private String dataPrivacyUrl;

  /**
   * Get the processed html template with replaced placeholders
   *
   * @param desc     the template description
   * @param name     the template name
   * @param data     the template data
   * @param language the template language
   * @return if success, an optional with the html template, otherwise an empty optional
   */
  public Optional<String> render(TemplateDescription desc, String name,
      Map<String, Object> data, LanguageCode language) throws TemplateServiceException {

    data.put("urlimpressum", imprintUrl);
    data.put("urldatenschutz", dataPrivacyUrl);

    List<String> missingFieldList = getMissingTemplateFields(desc, data);

    if (!CollectionUtils.isEmpty(missingFieldList)) {
      throw new TemplateServiceException(String.format(
          "Mail request for template %s could not be executed due to missing fields for template processing. Missing fields: %s",
          name, String.join(",", missingFieldList)));
    }

    var templateFilename = desc.getTemplateFilenameOrFallback(language);

    return ThymeleafHelper.getProcessedHtml(data, templateFilename);
  }

  /**
   * Get the processed subject with replaced placeholders
   *
   * @param templateDescription the mail template
   * @param templateData        the template data
   * @return the subject with replaced placeholders
   */
  public String getRenderedSubject(TemplateDescription templateDescription,
      Map<String, Object> templateData, LanguageCode languageCode) {
    StringSubstitutor stringSubstitutor = new StringSubstitutor(templateData, "${", "}");
    var subject = templateDescription.getSubjectOrFallback(languageCode);

    return stringSubstitutor.replace(subject);
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

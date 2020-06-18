package de.caritas.cob.MailService.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import de.caritas.cob.MailService.api.exception.ServiceException;
import de.caritas.cob.MailService.api.helper.ThymeleafHelper;
import de.caritas.cob.MailService.api.mailTemplate.TemplateDescription;

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
   * @param templateName the template name
   * @param templateData the template data
   * @return if success, an optional with the html template, otherwise an empty optional
   */
  public Optional<String> getProcessedHtmlTemplate(TemplateDescription templateDescription,
      String templateName, Map<String, Object> templateData) {

    templateData.put("urlimpressum", imprintUrl);
    templateData.put("urldatenschutz", dataPrivacyUrl);

    List<String> missingFieldList = getMissingTemplateFields(templateDescription, templateData);

    if (missingFieldList != null && !missingFieldList.isEmpty()) {
      throw new ServiceException(String.format(
          "Mail request for template %s could not be executed due to missing fields for template processing. Missing fields: %s",
          templateName,
          missingFieldList.stream().map(s -> s.toString()).collect(Collectors.joining(","))));
    }

    return ThymeleafHelper.getProcessedHtml(templateData,
        templateDescription.getHtmlTemplateFilename());

  }

  /**
   * Get the processed subject with replaced placeholders
   * 
   * @param templateDescription the mail template
   * @param templateData the template data
   * @return the subject with replaced placeholders
   */
  public String getProcessedSubject(TemplateDescription templateDescription,
      Map<String, Object> templateData) {
    StringSubstitutor stringSubstitutor = new StringSubstitutor(templateData, "${", "}");
    return stringSubstitutor.replace(templateDescription.getSubject());
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
      templateData = new HashMap<String, Object>();
    }
    List<String> missingFieldList = new ArrayList<String>();
    if (mailTemplate.getTemplateDataFields() != null
        && !mailTemplate.getTemplateDataFields().isEmpty()) {
      for (String fieldName : mailTemplate.getTemplateDataFields()) {
        if (!templateData.containsKey(fieldName)) {
          missingFieldList.add(fieldName);
        }
      }
    }
    return missingFieldList;
  }

}

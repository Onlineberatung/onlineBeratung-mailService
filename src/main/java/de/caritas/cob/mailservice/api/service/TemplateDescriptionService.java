package de.caritas.cob.mailservice.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.mailservice.api.exception.TemplateDescriptionServiceException;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for mail templates
 */
@Service
public class TemplateDescriptionService {

  private static final String TEMPLATE_DIR = "/templates/";
  private static final String TEMPLATE_EXTENSION = ".json";

  @Value("${template.use.custom.resources.path}")
  private boolean useCustomResourcesPath;

  @Value("${template.custom.resources.path}")
  private String customResourcePath;

  /**
   * Returns an instance of a mail template object
   *
   * @param templateName the name of the mail template
   * @return the mail template object
   */
  public Optional<TemplateDescription> getTemplateDescription(String templateName)
      throws TemplateDescriptionServiceException {

    return Optional.of(loadTemplateDescription(templateName));

  }

  /**
   * Load template description
   *
   * @param templateName the template name
   * @return the template description
   */
  private TemplateDescription loadTemplateDescription(String templateName)
      throws TemplateDescriptionServiceException {
    var mapper = new ObjectMapper();
    TemplateDescription templateDescription = null;
    String templateDescriptionJson = loadTemplateDescriptionFile(templateName);
    try {
      templateDescription = mapper.readValue(templateDescriptionJson, TemplateDescription.class);
    } catch (Exception ex) {
      throw new TemplateDescriptionServiceException(String.format(
          "Json file with template description could not be parsed, template name: %s",
          templateName), ex);
    }
    return templateDescription;
  }

  /**
   * Load template file from resources. InputStream is needed as file is located in jar.
   *
   * @param templateName the name of the template
   * @return the content of the template description file
   */
  private String loadTemplateDescriptionFile(String templateName)
      throws TemplateDescriptionServiceException {
    try (InputStream inputStream =
        useCustomResourcesPath ? new FileInputStream(
            customResourcePath + templateName.toLowerCase() + TEMPLATE_EXTENSION)
            : TemplateDescriptionService.class
                .getResourceAsStream(getTemplateFilename(templateName))) {
      final List<String> fileLines = IOUtils
          .readLines(inputStream, StandardCharsets.UTF_8.displayName());
      return String.join("", fileLines);
    } catch (Exception ex) {
      throw new TemplateDescriptionServiceException(String.format(
          "Json file with template description could not be loaded, template name: %s",
          templateName), ex);
    }
  }

  /**
   * Get the filename and filepath for the template description file
   *
   * @param templateName the template name
   * @return the filename with filepath of the template description file
   */
  private String getTemplateFilename(String templateName) {
    return TEMPLATE_DIR + templateName.toLowerCase() + TEMPLATE_EXTENSION;
  }

}

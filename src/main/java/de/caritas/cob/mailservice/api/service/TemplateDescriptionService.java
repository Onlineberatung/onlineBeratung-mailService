package de.caritas.cob.mailservice.api.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.mailservice.api.exception.ServiceException;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;

/**
 * Service for mail templates
 */
@Service
public class TemplateDescriptionService {

  private static final String TEMPLATE_DIR = "/templates/";
  private static final String TEMPLATE_EXTENSION = ".json";

  /**
   * Returns an instance of a mail template object
   * 
   * @param templateName the name of the mail template
   * @return the mail template object
   */
  public Optional<TemplateDescription> getTemplateDescription(String templateName) {

    return Optional.of(loadTemplateDescription(templateName));

  }

  /**
   * Load template description
   * 
   * @param templateName the template name
   * @return the template description
   */
  private TemplateDescription loadTemplateDescription(String templateName) {
    ObjectMapper mapper = new ObjectMapper();
    TemplateDescription templateDescription = null;
    String templateDescriptionJson = loadTemplateDescriptionFile(templateName);
    try {
      templateDescription = mapper.readValue(templateDescriptionJson, TemplateDescription.class);
    } catch (Exception ex) {
      throw new ServiceException(String.format(
          "Json file with template description could not be parsed, template name: %s",
          templateName), ex);
    }
    return templateDescription;
  }

  /**
   * Load template file from resources. InputStream is needed as file is located in jar.
   * 
   * @param templateName
   * @return the content of the template description file
   */
  private String loadTemplateDescriptionFile(String templateName) {
    InputStream in =
        TemplateDescriptionService.class.getResourceAsStream(getTemplateFilename(templateName));

    try {
      final List<String> fileLines = IOUtils.readLines(in, StandardCharsets.UTF_8.displayName());
      return fileLines.stream().collect(Collectors.joining());
    } catch (Exception ex) {
      throw new ServiceException(String.format(
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

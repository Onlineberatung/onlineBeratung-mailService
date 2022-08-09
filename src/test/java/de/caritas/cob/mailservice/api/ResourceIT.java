package de.caritas.cob.mailservice.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.mailservice.api.mailtemplate.TemplateDescription;
import de.caritas.cob.mailservice.api.model.MailDTO;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("testing")
class ResourceIT {

  private static final String TEMPLATES_DIR = ResourcePatternResolver.CLASSPATH_URL_PREFIX
      + "templates/";
  private static final String DESC_FILE_PATTERN = "*.json";

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ResourcePatternResolver resourceResolver;

  @Test
  void descriptionShouldDefineDefaultLanguageAndExistingTemplate() throws IOException {
    var jsonFiles = resourceResolver.getResources(TEMPLATES_DIR + DESC_FILE_PATTERN);
    var defaultLanguage = new MailDTO().getLanguage();
    assertTrue(jsonFiles.length > 8);

    for (Resource jsonFile : jsonFiles) {
      var json = Files.readString(jsonFile.getFile().toPath());
      var desc = objectMapper.readValue(json, TemplateDescription.class);
      var templates = desc.getHtmlTemplateFilename();
      assertTrue(templates.containsKey(defaultLanguage));

      var templateFilename = templates.get(defaultLanguage);
      var templatePath = TEMPLATES_DIR + templateFilename;
      var templateResource = resourceResolver.getResource(templatePath);
      assertTrue(templateResource.exists());
    }
  }

  @Test
  void descriptionShouldDeclareExistingTemplates() throws IOException {
    var jsonFiles = resourceResolver.getResources(TEMPLATES_DIR + DESC_FILE_PATTERN);
    assertTrue(jsonFiles.length > 8);

    for (Resource jsonFile : jsonFiles) {
      var json = Files.readString(jsonFile.getFile().toPath());
      var desc = objectMapper.readValue(json, TemplateDescription.class);
      var templates = desc.getHtmlTemplateFilename();
      assertTrue(templates.size() > 0);

      templates.forEach((languageCode, templateFilename) -> {
        var templatePath = TEMPLATES_DIR + templateFilename;
        var templateResource = resourceResolver.getResource(templatePath);
        assertTrue(templateResource.exists());
      });
    }
  }

  @Test
  void descriptionShouldDeclareDistinctTemplates() throws IOException {
    var jsonFiles = resourceResolver.getResources(TEMPLATES_DIR + DESC_FILE_PATTERN);
    assertTrue(jsonFiles.length > 8);

    for (Resource jsonFile : jsonFiles) {
      var json = Files.readString(jsonFile.getFile().toPath());
      var desc = objectMapper.readValue(json, TemplateDescription.class);
      var templates = desc.getHtmlTemplateFilename();

      var distinctTemplates = new HashSet<>(templates.values());
      assertTrue(templates.size() > 0);
      assertEquals(templates.size(), distinctTemplates.size());
    }
  }

  @Test
  void descriptionShouldDeclareDistinctLanguages() throws IOException {
    var jsonFiles = resourceResolver.getResources(TEMPLATES_DIR + DESC_FILE_PATTERN);
    assertTrue(jsonFiles.length > 8);

    for (Resource jsonFile : jsonFiles) {
      var json = Files.readString(jsonFile.getFile().toPath());
      var desc = objectMapper.readValue(json, TemplateDescription.class);
      var templates = desc.getHtmlTemplateFilename();

      assertTrue(templates.size() > 0);
      assertEquals(templates.size(), templates.keySet().size());
    }
  }

  @Test
  void descriptionShouldHaveDefaultSubject() throws IOException {
    var jsonFiles = resourceResolver.getResources(TEMPLATES_DIR + DESC_FILE_PATTERN);
    var defaultLanguage = new MailDTO().getLanguage();
    assertTrue(jsonFiles.length > 8);

    for (Resource jsonFile : jsonFiles) {
      var json = Files.readString(jsonFile.getFile().toPath());
      var desc = objectMapper.readValue(json, TemplateDescription.class);
      var subjects = desc.getSubject();
      assertNotNull(subjects.get(defaultLanguage));
    }
  }

  @Test
  void descriptionSubjectsAndTemplateFilesShouldBeCorresponding() throws IOException {
    var jsonFiles = resourceResolver.getResources(TEMPLATES_DIR + DESC_FILE_PATTERN);
    assertTrue(jsonFiles.length > 8);

    for (Resource jsonFile : jsonFiles) {
      var json = Files.readString(jsonFile.getFile().toPath());
      var desc = objectMapper.readValue(json, TemplateDescription.class);
      var templateLanguages = desc.getHtmlTemplateFilename().keySet();
      var subjectLanguages = desc.getSubject().keySet();

      assertEquals(templateLanguages, subjectLanguages);
    }
  }
}

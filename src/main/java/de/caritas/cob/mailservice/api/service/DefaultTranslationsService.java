package de.caritas.cob.mailservice.api.service;

import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.config.apiclient.TranlationMangementServiceApiClient;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DefaultTranslationsService {

  public String fetchDefaultTranslations(String translationComponentName, String languageCode,
      Dialect dialect) {
    InputStream inputStream = tryFetchDefaultTranslationWithFallbackToEmptyDialect(translationComponentName, languageCode, dialect);
    if (inputStream == null) {
      return "{}";
    }
    try {
      final List<String> fileLines = IOUtils
          .readLines(inputStream, StandardCharsets.UTF_8.displayName());
      return String.join("", fileLines);
    } catch (IOException ex) {
      throw new IllegalStateException(String.format(
          "Json file with translations could not be loaded, translation component name: %s",
          translationComponentName), ex);
    }
  }

  private InputStream tryFetchDefaultTranslationWithFallbackToEmptyDialect(String translationComponentName, String languageCode,
      Dialect dialect) {
    InputStream inputStream = getInputStream(translationComponentName, languageCode,
        dialect);
    if (inputStream == null) {
      log.warn(
          "Default translations for component {}, language {}, dialect {} not found in resources. Will try to fallback to default translations for empty dialect.",
          translationComponentName,
          languageCode, dialect);

      inputStream = getInputStream(translationComponentName, languageCode, null);
      if (inputStream == null) {
        log.warn(
            "Default translations for component {}, language {} and empty dialect not found in resources. Returning empty translations.",
            translationComponentName,
            languageCode);
        return null;
      }
    }
    return inputStream;
  }

  private InputStream getInputStream(String translationComponentName, String languageCode,
      Dialect dialect) {
    String translationFilename = getTranslationFilename(
        translationComponentName + "." + languageCode
            + TranlationMangementServiceApiClient.getDialectSuffix(dialect));
    return TranslationService.class.getResourceAsStream(translationFilename);
  }

  private String getTranslationFilename(String templateName) {
    return "/i18n/" + templateName.toLowerCase() + ".json";
  }
}

package de.caritas.cob.mailservice.api.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import de.caritas.cob.mailservice.api.model.Dialect;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DefaultTranslationsService {

  @Value("${template.custom.default.translations.path}")
  private String customTranslationsPath;

  @Value("${template.use.custom.resources.path}")
  private boolean useCustomResourcesPath;

  public String fetchDefaultTranslations(String translationComponentName, String languageCode,
      Dialect dialect) {
    InputStream inputStream = useCustomResourcesPath ? tryFetchExternalTranslationWithFallbackToEmptyDialect(translationComponentName, languageCode, dialect) :
            tryFetchDefaultTranslationWithFallbackToEmptyDialect(translationComponentName, languageCode, dialect);
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

  private InputStream tryFetchExternalTranslationWithFallbackToEmptyDialect(String translationComponentName, String languageCode,
          Dialect dialect) {
    InputStream inputStream = buildStreamForExternalPath(translationComponentName, languageCode, dialect);
    return inputStream != null ? inputStream : buildStreamForExternalPath(translationComponentName, languageCode, null);
  }

  private FileInputStream buildStreamForExternalPath(String translationComponentName, String languageCode, Dialect dialect) {
    try {
      String filename = String.format("%s/%s.%s%s.json", customTranslationsPath, translationComponentName.toLowerCase(), languageCode, getDialectSuffix(dialect));
      return new FileInputStream(filename);
    } catch (FileNotFoundException e) {
      log.warn("Default translations for component {}, language {} not found in external path {}", translationComponentName,
              languageCode, customTranslationsPath);
      return null;
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
            + getDialectSuffix(dialect));
    return TranslationService.class.getResourceAsStream(translationFilename);
  }

  public static String getDialectSuffix(Dialect dialect) {
    if (dialect == null) {
      return StringUtils.EMPTY;
    }
    if (dialect == Dialect.INFORMAL) {
      return "_informal";
    }
    return StringUtils.EMPTY;
  }

  private String getTranslationFilename(String templateName) {
    return "/i18n/" + templateName.toLowerCase() + ".json";
  }
}

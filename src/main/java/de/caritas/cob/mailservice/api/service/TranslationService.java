package de.caritas.cob.mailservice.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.config.apiclient.TranlationMangementServiceApiClient;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@Service
@Slf4j
public class TranslationService {

  @Value("${weblate.api.url}")
  private String apiUrl;

  @Value("${weblate.api.key}")
  private String apiKey;

  @Value("${weblate.project}")
  private String project;

  @Value("${weblate.component}")
  private String component;

  private final @NonNull TranlationMangementServiceApiClient tranlationMangementServiceApiClient;

  public TranslationService(
      TranlationMangementServiceApiClient tranlationMangementServiceApiClient) {
    this.tranlationMangementServiceApiClient = tranlationMangementServiceApiClient;
  }

  @Cacheable(value = "translations")
  public Map<String, String> fetchTranslations(String languageCode, Dialect dialect) {
    try {

      return fetchTranslationAsMap(languageCode, dialect);
    } catch (JsonProcessingException ex) {
      throw new TranslationServiceException(String.format(
          "Json file with translations could not be parsed, translation component name: %s",
          component), ex);
    }
  }

  @CacheEvict(value = "translations", allEntries = true)
  public void evictCache() {
    log.info("Evicting translations cache");
  }

  private Map<String, String> fetchTranslationAsMap(String languageCode, Dialect dialect)
      throws JsonProcessingException {
    String translations = fetchTranslationsAsString(languageCode, dialect);
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(translations, Map.class);
  }

  @Cacheable(value = "optionalTranslations")
  public Optional<Map<String, String>> tryFetchTranslations(String languageCode, Dialect dialect) {

    try {
      var result = fetchTranslationAsMap(languageCode, dialect);
      return result.isEmpty() ? Optional.empty() : Optional.of(result);
    } catch (JsonProcessingException e) {
      log.warn("Error while processing json file with translations. Returning empty translations",
          e);
      return Optional.empty();
    }
  }

  private String fetchTranslationsAsString(String languageCode, Dialect dialect) {
    try {
      return tranlationMangementServiceApiClient.tryFetchTranslationsFromTranslationManagementService(
          project, component,
          languageCode, dialect);
    } catch (HttpClientErrorException e) {
      if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
        log.warn(
            "Translations for component {}, language {} not found in weblate, returning default translations",
            component,
            languageCode);
        return fetchDefaultTranslations(component, languageCode, dialect);
      } else {
        log.error("Error while fetching translations from translation management service", e);
        throw e;
      }
    } catch (ResourceAccessException ex) {
      log.error("ResourceAccessException error while fetching translations from translation management service. Will fallback to resolve default translations.");
      log.debug("Exception details: ", ex);
      return fetchDefaultTranslations(component, languageCode, dialect);
    }
  }

  private String fetchDefaultTranslations(String translationComponentName, String languageCode, Dialect dialect) {
    var inputStream = TranslationService.class.getResourceAsStream(
        getTranslationFilename(translationComponentName + "." + languageCode + tranlationMangementServiceApiClient.getDialectSuffix(dialect)));
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

  private String getTranslationFilename(String templateName) {
    return "/i18n/" + templateName.toLowerCase() + ".json";
  }

  private class TranslationServiceException extends RuntimeException {

    public TranslationServiceException(String format, JsonProcessingException ex) {
      super(format, ex);
    }
  }
}

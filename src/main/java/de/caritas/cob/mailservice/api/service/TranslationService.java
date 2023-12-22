package de.caritas.cob.mailservice.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

  private final  @NonNull TranlationMangementServiceApiClient tranlationMangementServiceApiClient;

  public TranslationService(TranlationMangementServiceApiClient tranlationMangementServiceApiClient) {
    this.tranlationMangementServiceApiClient = tranlationMangementServiceApiClient;
  }

  @Cacheable(value = "translations")
  public Map<String, String> fetchTranslations(String languageCode) {
    try {

      return fetchTranslationAsMap(languageCode);
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

  private Map<String, String> fetchTranslationAsMap(String languageCode) throws JsonProcessingException {
    String translations = fetchTranslationsAsString(languageCode);
    ObjectMapper mapper = new ObjectMapper();
    return mapper.readValue(translations, Map.class);
  }

  @Cacheable(value = "optionalTranslations")
  public Optional<Map<String, String>> tryFetchTranslations(String languageCode) {

    try {
      var result = fetchTranslationAsMap(languageCode);
      return result.isEmpty() ? Optional.empty() : Optional.of(result);
    } catch (JsonProcessingException e) {
      log.warn("Error while processing json file with translations. Returning empty translations", e);
      return Optional.empty();
    }
  }

  private String fetchTranslationsAsString(String languageCode) {
    try {
      return tranlationMangementServiceApiClient.tryFetchTranslationsFromTranslationManagementService(project, component,
          languageCode);
    } catch (HttpClientErrorException e) {
      if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
        log.warn("Translations for component {}, language {} not found in weblate, returning default translations", component,
            languageCode);
        return fetchDefaultTranslations(component, languageCode);
      } else {
        log.error("Error while fetching translations from translation management service", e);
        throw e;
      }
    }
  }

  private String fetchDefaultTranslations(String translationComponentName, String languageCode) {
    var inputStream = TranslationService.class.getResourceAsStream(
        getTranslationFilename(translationComponentName + "." + languageCode));
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

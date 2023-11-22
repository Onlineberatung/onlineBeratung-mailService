package de.caritas.cob.mailservice.api.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.annotations.Cacheable;

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

  private final RestTemplate restTemplate;

  public TranslationService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
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

  public Optional<Map<String, String>> tryFetchTranslations(String languageCode) {

    try {
      var result = fetchTranslationAsMap(languageCode);
      return Optional.of(result);
    } catch (JsonProcessingException e) {
      log.warn("Error while processing json file with translations. Returning empty translations", e);
      return Optional.empty();
    }

  }

  private String fetchTranslationsAsString(String languageCode) {
    try {
      return tryFetchTranslationsFromTranslationManagementService(project, component,
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

  private String tryFetchTranslationsFromTranslationManagementService(String project,
      String component, String languageCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + apiKey);
    String url = apiUrl + "translations/" + project + "/" + component + "/test" + "/" + languageCode
        + "/file.json";

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
        new HttpEntity<>(headers), String.class);
    return response.getBody();
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
      throw new RuntimeException(String.format(
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

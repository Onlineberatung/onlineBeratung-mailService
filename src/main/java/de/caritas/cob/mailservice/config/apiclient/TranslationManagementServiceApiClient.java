package de.caritas.cob.mailservice.config.apiclient;

import de.caritas.cob.mailservice.api.model.Dialect;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TranslationManagementServiceApiClient {

  @Value("${weblate.api.url}")
  private String apiUrl;

  @Value("${weblate.api.key}")
  private String apiKey;

  private final  @NonNull RestTemplate restTemplate;

  public String tryFetchTranslationsFromTranslationManagementService(String project,
      String component, String languageCode, Dialect dialect) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + apiKey);
    String url = apiUrl + "translations/" + project + "/" + component + "/" + languageCode + getTranslationManagementServiceDialectSuffix(dialect)
        + "/file.json";

    log.info("Calling url to fetch translations: {}", url);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
        new HttpEntity<>(headers), String.class);
    return response.getBody();
  }

  public static String getTranslationManagementServiceDialectSuffix(Dialect dialect) {
    if (dialect == null) {
      return StringUtils.EMPTY;
    }
    if (dialect == Dialect.INFORMAL) {
      return "@informal";
    }
    return StringUtils.EMPTY;
  }
}

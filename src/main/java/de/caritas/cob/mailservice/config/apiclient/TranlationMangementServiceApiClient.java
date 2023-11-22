package de.caritas.cob.mailservice.config.apiclient;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class TranlationMangementServiceApiClient {

  @Value("${weblate.api.url}")
  private String apiUrl;

  @Value("${weblate.api.key}")
  private String apiKey;



  private final  @NonNull RestTemplate restTemplate;

  public String tryFetchTranslationsFromTranslationManagementService(String project,
      String component, String languageCode) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", "Token " + apiKey);
    String url = apiUrl + "translations/" + project + "/" + component + "/test" + "/" + languageCode
        + "/file.json";

    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,
        new HttpEntity<>(headers), String.class);
    return response.getBody();
  }
}

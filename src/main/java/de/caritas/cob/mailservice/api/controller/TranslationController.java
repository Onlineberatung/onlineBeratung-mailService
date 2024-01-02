package de.caritas.cob.mailservice.api.controller;

import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.api.service.TranslationService;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class TranslationController {

  private final @NonNull TranslationService translationService;

  @GetMapping(value = "/translations")
  public ResponseEntity<Map<String, String>> getTranslations() {
    var result = translationService.fetchTranslations("de");
    return new ResponseEntity<>(result, org.springframework.http.HttpStatus.OK);
  }

  @GetMapping(value = "/translations/{dialect}")
  public Map<String, String> getTranslations(@PathVariable Dialect dialect) {
    return translationService.fetchTranslations("de", dialect);
  }

  @GetMapping(value = "/translations/evict")
  public String evictTranslationCache() {
    translationService.evictCache();
    return "Cache evicted";
  }

}

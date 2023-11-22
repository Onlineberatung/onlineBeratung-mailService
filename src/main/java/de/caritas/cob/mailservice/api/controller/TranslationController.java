package de.caritas.cob.mailservice.api.controller;

import de.caritas.cob.mailservice.api.service.TranslationService;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequiredArgsConstructor
public class TranslationController {

  private final @NonNull TranslationService translationService;

  @RequestMapping(value = "/translations")
  public ResponseEntity<Map<String, String>> getTranslations() {
    var result = translationService.fetchTranslations("de");
    return new ResponseEntity<>(result, org.springframework.http.HttpStatus.OK);
  }

  @RequestMapping(value = "/translations/evict")
  public ResponseEntity<Void> evictTranslationCache() {
    translationService.evictCache();
    return new ResponseEntity<>(HttpStatus.OK);
  }

}

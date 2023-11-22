package de.caritas.cob.mailservice.api.controller;

import de.caritas.cob.mailservice.api.service.TranslationService;
import java.util.Map;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
@RequiredArgsConstructor
public class TranslationController {

  private final @NonNull TranslationService translationService;

  @RequestMapping(value = "/translations")
  public ResponseEntity<Map<String, String>> getTranslations() {
    var result = translationService.fetchTranslations("de");
    return new ResponseEntity<>(result, org.springframework.http.HttpStatus.OK);
  }

  @RequestMapping(value = "/translations/{lang}")
  public ResponseEntity<Map<String, String>> getTranslations(@PathVariable("lang") String languageCode) {
    var result = translationService.fetchTranslations(languageCode);
    return new ResponseEntity<>(result, org.springframework.http.HttpStatus.OK);
  }

  @RequestMapping(value = "/translations/evict")
  @ResponseBody
  public String evictTranslationCache() {
    translationService.evictCache();
    return "Cache evicted";
  }

}

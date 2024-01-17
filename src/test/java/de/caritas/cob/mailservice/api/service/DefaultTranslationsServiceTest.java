package de.caritas.cob.mailservice.api.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.caritas.cob.mailservice.api.model.Dialect;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("testing")
class DefaultTranslationsServiceTest {

  @Autowired
  DefaultTranslationsService defaultTranslationsService;

  @Test
  void fetchDefaultTranslations_Should_FetchTranlsationsForInformalGerman() {

    String translations = defaultTranslationsService.fetchDefaultTranslations("mailservice", "de",
        Dialect.INFORMAL);

    assertThat(translations).isNotNull();
    assertThat(translations).contains("hat Dir {0} als neuen Ratsuchenden zugewiesen.");
  }

  @Test
  void fetchDefaultTranslations_Should_FetchTranlsationsForFormalIfDialectNullGerman() {

    String translations = defaultTranslationsService.fetchDefaultTranslations("mailservice", "de",
        null);

    assertThat(translations).isNotNull();
    assertThat(translations).contains("hat Ihnen {0} als neuen Ratsuchenden zugewiesen.");
  }

  @Test
  void fetchDefaultTranslations_Should_FetchTranlsationsForEnglish() {

    String translations = defaultTranslationsService.fetchDefaultTranslations("mailservice", "en",
        null);

    assertThat(translations).isNotNull();
    assertThat(translations).contains("has assigned you {0} new advice seeker.");
  }

}
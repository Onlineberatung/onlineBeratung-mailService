package de.caritas.cob.mailservice.api.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import de.caritas.cob.mailservice.api.model.Dialect;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@ActiveProfiles("testing")
class DefaultTranslationsServiceIT {

  @Autowired
  private DefaultTranslationsService defaultTranslationsService;

  public final String USE_CUSTOM_RESOURCES_PATH_FIELD_NAME = "useCustomResourcesPath";
  public final String CUSTOM_TRANSLATIONS_PATH_FIELD_NAME = "customTranslationsPath";

  @BeforeEach
  public void setUp() {
    defaultTranslationsService = new DefaultTranslationsService();
    ReflectionTestUtils.setField(defaultTranslationsService, CUSTOM_TRANSLATIONS_PATH_FIELD_NAME, System.getProperty("user.dir") + "/src/main/resources/i18n");
  }

  @AfterEach
  public void clean() {
    ReflectionTestUtils.setField(defaultTranslationsService, USE_CUSTOM_RESOURCES_PATH_FIELD_NAME, false);
  }

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

  @Test
  void fetchDefaultTranslations_Should_fetchExternalTranslationsForFormalIfDialectNullGerman_When_useCustomResourcesPath_is_true(){

    ReflectionTestUtils.setField(defaultTranslationsService, USE_CUSTOM_RESOURCES_PATH_FIELD_NAME, true);

    String translations = defaultTranslationsService.fetchDefaultTranslations("mailservice", "de",
        null);

    assertThat(translations).isNotNull();
    assertThat(translations).contains("Sie haben eine neue Nachricht in Ihren Beratungen");

  }

  @Test
  void fetchDefaultTranslations_Should_fetchExternalTranslationsForEnglish_When_useCustomResourcesPath_is_true(){

    ReflectionTestUtils.setField(defaultTranslationsService, USE_CUSTOM_RESOURCES_PATH_FIELD_NAME, true);

    String translations = defaultTranslationsService.fetchDefaultTranslations("mailservice", "en",
        null);

    assertThat(translations).isNotNull();
    assertThat(translations).contains("You have a new message in your counselings");

  }

  @Test
  void fetchDefaultTranslations_Should_fetchExternalTranslationsForInformalGerman_When_useCustomResourcesPath_is_true(){

    ReflectionTestUtils.setField(defaultTranslationsService, USE_CUSTOM_RESOURCES_PATH_FIELD_NAME, true);

    String translations = defaultTranslationsService.fetchDefaultTranslations("mailservice", "de",
        Dialect.INFORMAL);

    assertThat(translations).isNotNull();
    assertThat(translations).contains("Du hast eine neue Nachricht in Deinen Beratungen");

  }

}

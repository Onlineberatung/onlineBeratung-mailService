package de.caritas.cob.mailservice.api.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.config.apiclient.TranslationManagementServiceApiClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TranslationServiceTest {

  private TranslationService translationService;

  @Mock
  private TranslationManagementServiceApiClient translationManagementServiceApiClient;

  @Mock
  private DefaultTranslationsService defaultTranslationsService;

  public final String TRANSLATION_MANAGEMENT_SYSTEM_ENABLED_FIELD_NAME = "translationManagementSystemEnabled";

  @BeforeEach
  public void setUp() {
    translationService = new TranslationService(translationManagementServiceApiClient, defaultTranslationsService);
  }

  @Test
  void fetchTranslations_Should_call_tryFetchTranslationsFromTranslationManagementService_When_translationManagementSystemEnabled_is_true() {

    //Arrange
    final boolean TRANSLATION_MANAGEMENT_SYSTEM_ENABLED_FIELD_VALUE = true;
    ReflectionTestUtils.setField(translationService, TRANSLATION_MANAGEMENT_SYSTEM_ENABLED_FIELD_NAME, TRANSLATION_MANAGEMENT_SYSTEM_ENABLED_FIELD_VALUE);
    when(translationManagementServiceApiClient.tryFetchTranslationsFromTranslationManagementService(any(), any(), any(), any())).thenReturn("{\"mail.label.header\": \"Beratung\"}");

    //Act
    translationService.fetchTranslations("test", Dialect.INFORMAL);

    //Assert
    verify(translationManagementServiceApiClient).tryFetchTranslationsFromTranslationManagementService(any(), any(), any(), any());
    verifyNoInteractions(defaultTranslationsService);

  }

  @Test
  void fetchTranslations_Should_call_fetchDefaultTranslations_When_translationManagementSystemEnabled_is_false() {

    //Arrange
    final boolean TRANSLATION_MANAGEMENT_SYSTEM_ENABLED_FIELD_VALUE = false;
    ReflectionTestUtils.setField(translationService, TRANSLATION_MANAGEMENT_SYSTEM_ENABLED_FIELD_NAME, TRANSLATION_MANAGEMENT_SYSTEM_ENABLED_FIELD_VALUE);
    when(defaultTranslationsService.fetchDefaultTranslations(any(), any(), any())).thenReturn("{\"mail.label.header\": \"Beratung\"}");

    //Act
    translationService.fetchTranslations("test", Dialect.INFORMAL);

    //Assert
    verifyNoInteractions(translationManagementServiceApiClient);
    verify(defaultTranslationsService).fetchDefaultTranslations(any(), any(), any());

  }

}

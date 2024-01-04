package de.caritas.cob.mailservice.api;

import static org.mockito.Mockito.verify;

import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.api.service.TranslationService;
import java.util.Locale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslationMessageSourceTest {

  private static final String INFORMAL_GERMAL_LANGUAGE_TAG = "de-DE-u-va-posix";

  @InjectMocks
  private TranslationMessageSource translationMessageSource;

  @Mock
  private TranslationService translationService;

  @Test
  void getMessage_Should_CallFetchTranslations_With_FormalDialect_For_DefaultLocale() {
    // given
    Locale locale = Locale.getDefault();

    // when
    translationMessageSource.getMessage("translation_key", new Object[]{}, "Message", locale);

    // then
    verify(translationService, Mockito.times(1)).fetchTranslations(locale.getLanguage(), Dialect.FORMAL);
  }

  @Test
  void getMessage_Should_CallFetchTranslations_With_InformalDialect_For_ForLocaleForInformalGerman() {
    // given
    Locale locale = Locale.forLanguageTag(INFORMAL_GERMAL_LANGUAGE_TAG);

    // when
    translationMessageSource.getMessage("translation_key", new Object[]{}, "Message", locale);

    // then
    verify(translationService, Mockito.times(1)).fetchTranslations(locale.getLanguage(), Dialect.INFORMAL);
  }

}
package de.caritas.cob.mailservice.api;

import de.caritas.cob.mailservice.api.service.TranslationService;
import java.util.Locale;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestApiMessageSource implements MessageSource {


  public final @NonNull TranslationService translationService;

  @Override
  public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
    log.info("getMessage called with code: {}, args: {}, defaultMessage: {}, locale: {}", code,
        args, defaultMessage, locale);
    return translationService.fetchTranslations(locale.getLanguage()).get(code);
  }

  @Override
  public String getMessage(String code, Object[] args, Locale locale)
      throws NoSuchMessageException {
    return getMessage(code, args, null, locale);
  }

  @Override
  public String getMessage(MessageSourceResolvable resolvable, Locale locale)
      throws NoSuchMessageException {
    if (resolvable == null) {
      log.warn("getMessage called with null resolvable");
      return null;
    }
    return getMessage(resolvable.getCodes()[0], null, locale);
  }
}

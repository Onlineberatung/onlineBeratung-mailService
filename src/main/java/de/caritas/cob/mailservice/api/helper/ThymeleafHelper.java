package de.caritas.cob.mailservice.api.helper;

import de.caritas.cob.mailservice.api.model.Dialect;
import de.caritas.cob.mailservice.api.model.LanguageCode;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class ThymeleafHelper {

  private static final String INFORMAL_GERMAL_LANGUAGE_TAG = "de-DE-u-va-posix";

  @Autowired
  private TemplateEngine tempTemplateEngine;

  private static TemplateEngine templateEngine;

  @PostConstruct
  void init() {
    templateEngine = tempTemplateEngine;
  }

  public static Optional<String> getProcessedHtml(Map<String, Object> data, LanguageCode languageCode, String templateName, Dialect dialect) {

    Context context = new Context();
    Locale locale = Locale.forLanguageTag(getLanguageTag(languageCode, dialect));
    context.setLocale(locale);
    if (data != null) {
      data.forEach(context::setVariable);
      return Optional.of(templateEngine.process(templateName, context));
    }
    return Optional.empty();

  }

  private static String getLanguageTag(LanguageCode languageCode, Dialect dialect) {
    if (languageCode == LanguageCode.DE && dialect == Dialect.INFORMAL) {
      return INFORMAL_GERMAL_LANGUAGE_TAG;
    }
    return languageCode.getValue();
  }
}

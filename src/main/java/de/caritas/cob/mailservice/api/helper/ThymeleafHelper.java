package de.caritas.cob.mailservice.api.helper;

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

  @Autowired
  private TemplateEngine tempTemplateEngine;

  private static TemplateEngine templateEngine;

  @PostConstruct
  void init() {
    templateEngine = tempTemplateEngine;
  }

  public static Optional<String> getProcessedHtml(Map<String, Object> data, LanguageCode languageCode, String templateName) {

    Context context = new Context();
    Locale locale = Locale.forLanguageTag(languageCode.getValue());
    context.setLocale(locale);

    if (data != null) {
      data.forEach(context::setVariable);
      return Optional.of(templateEngine.process(templateName, context));
    }
    return Optional.empty();

  }
}

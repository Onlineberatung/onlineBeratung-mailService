package de.caritas.cob.mailservice.api.helper;

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

  public static Optional<String> getProcessedHtml(Map<String, Object> data, String templateName) {

    Context context = new Context();

    if (data != null) {
      data.forEach(context::setVariable);
      return Optional.of(templateEngine.process(templateName, context));
    }
    return Optional.empty();

  }
}

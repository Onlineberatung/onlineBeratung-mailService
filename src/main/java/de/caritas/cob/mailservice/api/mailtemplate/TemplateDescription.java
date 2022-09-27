package de.caritas.cob.mailservice.api.mailtemplate;

import de.caritas.cob.mailservice.api.model.LanguageCode;
import de.caritas.cob.mailservice.api.model.MailDTO;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representation for mail a mail template
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TemplateDescription {

  private Map<LanguageCode, String> htmlTemplateFilename;
  private Map<LanguageCode, String> subject;
  private List<String> templateDataFields;
  private List<TemplateImage> templateImages;

  private static LanguageCode defaultLanguage() {
    return new MailDTO().getLanguage();
  }

  public String getSubjectOrFallback(LanguageCode language) {
    return subject.containsKey(language)
        ? subject.get(language)
        : subject.get(defaultLanguage());
  }

  public String getTemplateFilenameOrFallback(LanguageCode language) {
    return htmlTemplateFilename.containsKey(language)
        ? htmlTemplateFilename.get(language)
        : htmlTemplateFilename.get(defaultLanguage());
  }
}

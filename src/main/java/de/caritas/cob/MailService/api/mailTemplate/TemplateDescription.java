package de.caritas.cob.MailService.api.mailTemplate;

import java.util.List;
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

  private String htmlTemplateFilename;
  private String subject;
  private List<String> templateDataFields;
  private List<TemplateImage> templateImages;

}

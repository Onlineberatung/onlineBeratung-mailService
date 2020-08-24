package de.caritas.cob.mailservice.api.mailtemplate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representation for an image in a mail template
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TemplateImage {

  private String filename;
  private String filetype;

}

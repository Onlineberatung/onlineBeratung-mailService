package de.caritas.cob.MailService.api.model;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for a mail
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(value = "Mail")
public class MailDTO {

  @NotBlank(message = "{mail.template.notBlank}")
  @ApiModelProperty(required = true, example = "template", position = 0)
  @JsonProperty("template")
  private String template;

  @NotBlank(message = "{mail.email.notBlank}")
  @Email(message = "{mail.email.invalid}")
  @ApiModelProperty(required = true, example = "max@mustermann.de", position = 1)
  @JsonProperty("email")
  private String email;

  @Valid
  @ApiModelProperty(required = false, position = 2)
  @JsonProperty("templateData")
  private List<TemplateDataDTO> templateData;

}

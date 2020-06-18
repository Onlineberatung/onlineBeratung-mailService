package de.caritas.cob.MailService.api.model;

import javax.validation.constraints.NotBlank;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for template placeholders
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(value = "TemplateData")
public class TemplateDataDTO {

  @NotBlank(message = "{template.data.key.notBlank}")
  @ApiModelProperty(required = true, example = "name", position = 0)
  @JsonProperty("key")
  private String key;

  @NotBlank(message = "{template.data.value.notBlank}")
  @ApiModelProperty(required = true, position = 1)
  @JsonProperty("value")
  private String value;

}

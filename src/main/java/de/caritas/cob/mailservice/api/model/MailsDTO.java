package de.caritas.cob.mailservice.api.model;

import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ApiModel(value = "Mails")
public class MailsDTO {

  @NotEmpty(message = "{mails.notEmpty}")
  @ApiModelProperty(required = true, position = 0)
  @JsonProperty("mails")
  @Valid
  private List<MailDTO> mails;

}

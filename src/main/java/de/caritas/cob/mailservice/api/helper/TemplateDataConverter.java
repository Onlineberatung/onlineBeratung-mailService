package de.caritas.cob.mailservice.api.helper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import de.caritas.cob.mailservice.api.model.TemplateDataDTO;
import org.springframework.util.CollectionUtils;

/**
 * Converter for TemplateDataDTO
 */
@Component
public class TemplateDataConverter {

  private Helper helper;

  public TemplateDataConverter(Helper helper) {
    this.helper = helper;
  }

  /**
   * Converts a {@link TemplateDataDTO} list to a map
   * 
   * @param templateDataDTOList The list with objects of {@link TemplateDataDTO}
   * @return the map
   */
  public Map<String, Object> convertFromTemplateDataDTOList(
      List<TemplateDataDTO> templateDataDTOList) {

    Map<String, Object> templateData = new HashMap<>();

    if (!CollectionUtils.isEmpty(templateDataDTOList)) {
      templateData = templateDataDTOList.stream()
          .collect(Collectors.toMap(TemplateDataDTO::getKey, TemplateDataDTO::getValue));
    }

    return templateData.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
        e -> helper.unescapeHtml(helper.removeHTMLFromText(String.valueOf(e.getValue())))));

  }

}

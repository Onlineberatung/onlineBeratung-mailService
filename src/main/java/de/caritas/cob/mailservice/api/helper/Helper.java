package de.caritas.cob.mailservice.api.helper;

import javax.ws.rs.InternalServerErrorException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.springframework.stereotype.Component;

/**
 * Helper class
 *
 */
@Component
public class Helper {

  /**
   * 
   * Remove HTML code from a text (XSS-Protection)
   * 
   * @param text
   * @return the given text without html
   */
  public String removeHTMLFromText(String text) {

    OutputSettings outputSettings = new OutputSettings();
    outputSettings.prettyPrint(false);

    try {
      text = Jsoup.clean(text, StringUtils.EMPTY, Whitelist.none(), outputSettings);
    } catch (Exception exception) {
      throw new InternalServerErrorException("Error while removing HTML from text", exception);
    }
    return text;
  }

  public String unescapeHtml(String text) {
    return StringEscapeUtils.unescapeHtml4(text);
  }
}

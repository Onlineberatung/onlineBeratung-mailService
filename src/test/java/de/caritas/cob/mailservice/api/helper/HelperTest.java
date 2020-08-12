package de.caritas.cob.mailservice.api.helper;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HelperTest {

  private Helper helper;

  private static String TEXT = "Lorem Ipsum";
  private static String TEXT_WITH_NEWLINE = "Lorem Ipsum\nLorem Ipsum";
  private static String TEXT_WITH_NEWLINE_AND_HTML_AND_JS =
      "<b>Lorem Ipsum</b>\nLorem Ipsum<script>alert('1');</script>";
  private static String TEXT_WITH_HTML = "<strong>Lorem Ipsum</strong>";
  private static String TEXT_WITH_JS = "Lorem Ipsum<script>alert('1');</script>";
  private static String TEXT_WITH_HTML_ENTITY = "Hallo &amp;";
  private static String TEXT_WITH_UNESCAPED_HTML_ENTITY = "Hallo &";

  @Before
  public void setup() {
    helper = new Helper();
  }

  @Test
  public void removeHTMLFromText_Should_RemoveHtmlFromText() {
    assertEquals(TEXT, helper.removeHTMLFromText(TEXT_WITH_HTML));
  }

  @Test
  public void removeHTMLFromText_Should_RemoveJavascriptFromText() {
    assertEquals(TEXT, helper.removeHTMLFromText(TEXT_WITH_JS));
  }

  @Test
  public void removeHTMLFromText_ShouldNot_RemoveNewslinesFromText() {
    assertEquals(TEXT_WITH_NEWLINE, helper.removeHTMLFromText(TEXT_WITH_NEWLINE));
  }

  @Test
  public void removeHTMLFromText_Should_RemoveHtmlAndJavascriptFromText_And_ShouldNot_RemoveNewslines() {
    assertEquals(TEXT_WITH_NEWLINE, helper.removeHTMLFromText(TEXT_WITH_NEWLINE_AND_HTML_AND_JS));
  }

  @Test
  public void unescapeHtml_Should_ConvertHtmlEntity() {
    assertEquals(TEXT_WITH_UNESCAPED_HTML_ENTITY, helper.unescapeHtml(TEXT_WITH_HTML_ENTITY));
  }
}

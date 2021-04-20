package de.caritas.cob.mailservice.api.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import de.caritas.cob.mailservice.api.model.MailDTO;
import org.junit.Test;

public class MailErrorMessageBuilderTest {

  private final MailErrorMessageBuilder mailErrorMessageBuilder = new MailErrorMessageBuilder();
  private final MailDTO mailDTO = new MailDTO().template("template");

  @Test
  public void buildEmailErrorMessage_Should_returnErrorMessageWithoutEmail_When_emailIsContainedInStacktrace() {
    Exception exception = new RuntimeException("test@test.de");

    String errorMessage = this.mailErrorMessageBuilder.buildEmailErrorMessage(mailDTO, exception);

    assertThat(errorMessage, not(containsString("test@test.de")));
  }

  @Test
  public void buildEmailErrorMessage_Should_returnErrorMessageWithoutOnlyEmail_When_emailIsContainedInStacktrace() {
    Exception exception = new RuntimeException("address is test@test.de for given user");

    String errorMessage = this.mailErrorMessageBuilder.buildEmailErrorMessage(mailDTO, exception);

    assertThat(errorMessage, not(containsString("test@test.de")));
    assertThat(errorMessage, containsString("address is  for given user"));
  }

  @Test
  public void buildEmailErrorMessage_Should_returnErrorMessageWithCustomReason_When_noEmailIsContainedInReason() {
    Exception exception = new RuntimeException("My custom reason");

    String errorMessage = this.mailErrorMessageBuilder.buildEmailErrorMessage(mailDTO, exception);

    assertThat(errorMessage, containsString("My custom reason"));
  }

}

package de.caritas.cob.mailservice.api.controller;

import de.caritas.cob.mailservice.api.model.ErrorMailDTO;
import de.caritas.cob.mailservice.api.model.MailsDTO;
import de.caritas.cob.mailservice.api.service.MailService;
import de.caritas.cob.mailservice.generated.api.controller.MailsApi;
import io.swagger.annotations.Api;
import javax.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for mail requests.
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "mails-controller")
public class MailController implements MailsApi {

  private final @NonNull MailService mailService;

  /**
   * Entry point for mail sending.
   */
  @Override
  public ResponseEntity<Void> sendMails(@Valid @RequestBody MailsDTO mails) {
    this.mailService.sendHtmlMails(mails);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  /**
   * Entry point for error mail sending.
   */
  @Override
  public ResponseEntity<Void> sendErrorMail(@Valid ErrorMailDTO errorMailDTO) {
    this.mailService.sendErrorMailDto(errorMailDTO);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}

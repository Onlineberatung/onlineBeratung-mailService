package de.caritas.cob.mailservice.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@TestConfiguration
public class MailTestConfiguration {

  @Bean("emailsender") // need to define it for spring-actuator
  public JavaMailSender getJavaMailSender(@Value("${spring.mail.host}") String host,
      @Value("${spring.mail.port}") int port) {
    final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(host);
    javaMailSender.setPort(port);
    return javaMailSender;
  }
}

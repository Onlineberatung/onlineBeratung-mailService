package de.caritas.cob.mailservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Contains some general spring boot application configurations
 */
@Configuration
@ComponentScan(basePackages = {"de.caritas.cob.mailservice"})
public class AppConfig {

  /**
   * Activate the mails.properties for validation messages.
   *
   * @param messageSource
   * @return
   */
  @Bean
  public LocalValidatorFactoryBean validator(MessageSource messageSource) {
    LocalValidatorFactoryBean validatorFactoryBean = new LocalValidatorFactoryBean();
    validatorFactoryBean.setValidationMessageSource(messageSource);
    return validatorFactoryBean;
  }

  @Bean("emailsender") // need to define it for spring-actuator
  public JavaMailSender getJavaMailSender(@Value("${spring.mail.host}") String host,
      @Value("${spring.mail.port}") int port) {
    final JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
    javaMailSender.setHost(host);
    javaMailSender.setPort(port);
    return javaMailSender;
  }

}

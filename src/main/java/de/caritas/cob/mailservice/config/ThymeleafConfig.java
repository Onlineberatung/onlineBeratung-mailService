package de.caritas.cob.mailservice.config;

import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
public class ThymeleafConfig {

  @Value("${resourcePath}")
  private String resourcePath;

  @Value("${newResources}")
  private boolean newResources;


  /**
   * This method is use to get ClassLoaderTemplateResolver.
   * 
   * @return ClassLoaderTemplateResolver.
   */
  @Bean
  public ITemplateResolver templateResolver() {
    return newResources ? htmlFileTemplateResolver() : htmlClassLoaderTemplateResolver();
  }

  private ITemplateResolver htmlFileTemplateResolver() {
    FileTemplateResolver emailFileTemplateResolver = new FileTemplateResolver();
    emailFileTemplateResolver.setOrder(1);
    emailFileTemplateResolver.setPrefix(resourcePath);
    emailFileTemplateResolver.setSuffix(".html");
    emailFileTemplateResolver.setTemplateMode(TemplateMode.HTML);
    emailFileTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
    return emailFileTemplateResolver;
  }

  private ITemplateResolver htmlClassLoaderTemplateResolver() {
    ClassLoaderTemplateResolver emailClassLoaderTemplateResolver = new ClassLoaderTemplateResolver();
    emailClassLoaderTemplateResolver.setOrder(2);
    emailClassLoaderTemplateResolver.setPrefix("/templates/");
    emailClassLoaderTemplateResolver.setSuffix(".html");
    emailClassLoaderTemplateResolver.setTemplateMode(TemplateMode.HTML);
    emailClassLoaderTemplateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
    return emailClassLoaderTemplateResolver;
  }
}

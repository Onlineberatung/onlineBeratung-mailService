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

  @Value("${customResourcePath}")
  private String customResourcePath;

  @Value("${useCustomResourcesPath}")
  private boolean useCustomResourcesPath;


  /**
   * Based on the {@link ThymeleafConfig#useCustomResourcesPath} value this method creates the right template resolver.
   * useCustomResourcesPath == true -> {@link ThymeleafConfig#htmlFileTemplateResolver()}
   * useCustomResourcesPath == true -> {@link ThymeleafConfig#htmlClassLoaderTemplateResolver()}
   * 
   * @return ClassLoaderTemplateResolver.
   */
  @Bean
  public ITemplateResolver templateResolver() {
    return useCustomResourcesPath ? htmlFileTemplateResolver() : htmlClassLoaderTemplateResolver();
  }

  private ITemplateResolver htmlFileTemplateResolver() {
    FileTemplateResolver emailFileTemplateResolver = new FileTemplateResolver();
    emailFileTemplateResolver.setOrder(1);
    emailFileTemplateResolver.setPrefix(customResourcePath);
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

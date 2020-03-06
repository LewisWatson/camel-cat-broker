package com.github.lewiswatson.camelcatbroker.enricher.rules;

import com.github.lewiswatson.camelcatbroker.enricher.DestinationCatteryEnricher;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;
import com.google.gson.Gson;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.io.ResourceFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("rules-engine")
public class RulesEngineCatteryEnricherConfig {

  @Bean
  public DestinationCatteryEnricher destinationCatteryEnricher(CatRouterProperties properties,
      Gson gson) {

    KieServices kieServices = KieServices.Factory.get();
    KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
    kieFileSystem.write(ResourceFactory.newClassPathResource(properties.getRulesPath()));
    KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem);
    kieBuilder.buildAll();
    KieModule kieModule = kieBuilder.getKieModule();
    KieContainer kieContainer = kieServices.newKieContainer(kieModule.getReleaseId());

    return RulesEngineCatteryEnricher.builder().gson(gson).properties(properties)
        .kieContainer(kieContainer).build();
  }

}

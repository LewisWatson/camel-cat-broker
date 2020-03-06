package com.github.lewiswatson.camelcatbroker.enricher.simple;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.lewiswatson.camelcatbroker.enricher.DestinationCatteryEnricher;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;
import com.google.gson.Gson;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!rules-engine")
public class SimpleDestinationCatteryEnricherConfig {

  @Bean
  public DestinationCatteryEnricher destinationCatteryEnricher(CatRouterProperties properties,
      Gson gson) {
    return TabbiesGoToCatsRusDestinationCatteryEnricher.builder().properties(properties).gson(gson)
        .build();
  }

}

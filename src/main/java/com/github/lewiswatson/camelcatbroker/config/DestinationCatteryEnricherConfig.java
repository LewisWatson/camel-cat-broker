package com.github.lewiswatson.camelcatbroker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.lewiswatson.camelcatbroker.enricher.DestinationCatteryEnricher;
import com.github.lewiswatson.camelcatbroker.enricher.TabbiesGoToCatsRusDestinationCatteryEnricher;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;
import com.google.gson.Gson;

@Configuration
public class DestinationCatteryEnricherConfig {

  @Bean
  public DestinationCatteryEnricher destinationCatteryEnricher(CatRouterProperties properties,
      Gson gson) {
    return TabbiesGoToCatsRusDestinationCatteryEnricher.builder().properties(properties).gson(gson)
        .build();
  }

}

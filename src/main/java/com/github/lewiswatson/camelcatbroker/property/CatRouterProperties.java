package com.github.lewiswatson.camelcatbroker.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Component
@ConfigurationProperties(prefix="cat-router")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CatRouterProperties {
  
  @Builder.Default
  private String destinationCatteryHeader = "destination-cattery";

  private String rulesPath;

}

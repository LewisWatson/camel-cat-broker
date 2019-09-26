package com.github.lewiswatson.camelcatbroker.route;

import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.github.lewiswatson.camelcatbroker.enricher.DestinationCatteryEnricher;
import com.github.lewiswatson.camelcatbroker.model.Cattery;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;

@Service
public class CatBrokerRoute extends SpringRouteBuilder {

  @Autowired
  private CatRouterProperties properties;

  @Autowired
  private DestinationCatteryEnricher destinationCatteryEnricher;

  @Override
  public void configure() throws Exception {
    // @formatter:off
    from("{{cat-broker.route.cat-broker.from}}")
      .routeId("{{cat-broker.route.cat-broker.name}}")
      .process(destinationCatteryEnricher)
      .choice()
        .when(header(properties.getDestinationCatteryHeader()).isEqualTo(Cattery.CATS_R_US))
          .to("{{cat-broker.route.cat-broker.cats-r-us}}")
        .otherwise()
          .to("{{cat-broker.route.cat-broker.dlq}}");
    // @formatter:on
  }
}

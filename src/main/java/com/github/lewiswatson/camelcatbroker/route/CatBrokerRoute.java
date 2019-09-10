package com.github.lewiswatson.camelcatbroker.route;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;

@Component
public class CatBrokerRoute extends SpringRouteBuilder {

  private static final String CATS_R_US_CATTERY = "cats-r-us";

  @Autowired
  private CatRouterProperties properties;

  @Override
  public void configure() throws Exception {

    from("{{cat-broker.route.cat-broker.from}}").routeId("{{cat-broker.route.cat-broker.name}}")
        .process(new Processor() {
          @Override
          public void process(Exchange exchange) throws Exception {
            Message in = exchange.getIn();
            in.setHeader(properties.getDestinationCatteryHeader(), CATS_R_US_CATTERY);
          }
        }).choice()
        .when(header(properties.getDestinationCatteryHeader()).isEqualTo(CATS_R_US_CATTERY))
        .to("{{cat-broker.route.cat-broker.cats-r-us}}").otherwise().to("{{cat-broker.route.cat-broker.dlq}}");

  }
}

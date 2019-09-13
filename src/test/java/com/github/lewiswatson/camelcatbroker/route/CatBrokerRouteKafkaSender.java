package com.github.lewiswatson.camelcatbroker.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("kafka")
public class CatBrokerRouteKafkaSender extends RouteBuilder {
  
  public static final String TEST_CAT_BROKER_SENDER_ROUTE_ID = "test-cat-broker-sender";

  @Override
  public void configure() throws Exception {
    // @formatter:off
    from("direct:test-cat-broker-sender")
      .routeId(TEST_CAT_BROKER_SENDER_ROUTE_ID)
      .to("{{cat-broker.route.cat-broker.from}}");
    // @formatter:on
  }  
  
}

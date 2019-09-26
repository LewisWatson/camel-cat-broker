package com.github.lewiswatson.camelcatbroker.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExampleRoute extends RouteBuilder {

  public static final String EXAMPLE_ROUTE_ID = "example-route";

  @Override
  public void configure() throws Exception {
    from("direct:start").routeId(EXAMPLE_ROUTE_ID).filter(header("foo").isEqualTo("bar"))
        .to("mock:result");
  }

}

package com.github.lewiswatson.camelcatbroker.route;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExampleRoute extends RouteBuilder {

  @Override
  public void configure() throws Exception {
    from("direct:start").filter(header("foo").isEqualTo("bar")).to("mock:result");
  }

}

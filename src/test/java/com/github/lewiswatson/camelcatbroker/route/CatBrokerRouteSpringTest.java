package com.github.lewiswatson.camelcatbroker.route;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = {CatBrokerRouteSpringTest.ContextConfig.class})
@Ignore("need to figure out how to wire in properties")
public class CatBrokerRouteSpringTest {

  @EndpointInject(uri = "{{cat-broker.route.cat-broker.to}}")
  protected MockEndpoint resultEndpoint;

  @Produce(uri = "{{cat-broker.route.cat-broker.from}}")
  protected ProducerTemplate template;

  @DirtiesContext
  @Test
  public void testSendMatchingMessage() throws Exception {

    String expectedBody = "<matched/>";

    resultEndpoint.expectedBodiesReceived(expectedBody);
    resultEndpoint.expectedHeaderReceived("destination-psp", "example-psp-id");

    template.sendBodyAndHeader(expectedBody, "foo", "bar");

    resultEndpoint.assertIsSatisfied();
  }

  @Configuration
  public static class ContextConfig extends SingleRouteCamelConfiguration {
    
    @Override
    @Bean
    public RouteBuilder route() {
      return new CatBrokerRoute();
    }
  }

}

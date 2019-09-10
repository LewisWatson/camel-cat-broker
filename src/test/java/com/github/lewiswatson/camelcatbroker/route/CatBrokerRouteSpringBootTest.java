package com.github.lewiswatson.camelcatbroker.route;

import com.github.lewiswatson.camelcatbroker.CamelCatBrokerApplication;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.EnableRouteCoverage;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = CamelCatBrokerApplication.class)
@EnableRouteCoverage
public class CatBrokerRouteSpringBootTest {

  @Produce(uri="{{cat-broker.route.cat-broker.from}}")
  private ProducerTemplate testProducer;

  @EndpointInject(uri="{{cat-broker.route.cat-broker.cats-r-us}}")
  private MockEndpoint endEndpoint;
  
  @Autowired
  private CatRouterProperties properties;

  @Test
  public void testRoute() throws InterruptedException {

    endEndpoint.expectedMessageCount(1);
    endEndpoint.expectedHeaderReceived(properties.getDestinationCatteryHeader(), "cats-r-us");

    testProducer.sendBody("<name>test</name>");

    endEndpoint.assertIsSatisfied();

  }

}
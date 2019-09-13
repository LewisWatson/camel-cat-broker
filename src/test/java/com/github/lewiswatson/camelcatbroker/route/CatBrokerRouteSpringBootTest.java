package com.github.lewiswatson.camelcatbroker.route;

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
import com.github.lewiswatson.camelcatbroker.CamelCatBrokerApplication;
import com.github.lewiswatson.camelcatbroker.model.Cat;
import com.github.lewiswatson.camelcatbroker.model.Cat.Breed;
import com.github.lewiswatson.camelcatbroker.model.Cat.Temperment;
import com.google.gson.Gson;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CamelCatBrokerApplication.class})
@EnableRouteCoverage
public class CatBrokerRouteSpringBootTest {

  @Produce(uri = "{{cat-broker.route.cat-broker.from}}")
  private ProducerTemplate testProducer;

  @EndpointInject(uri = "{{cat-broker.route.cat-broker.cats-r-us}}")
  private MockEndpoint catsRusEndpoint;

  @EndpointInject(uri = "{{cat-broker.route.cat-broker.dlq}}")
  private MockEndpoint dlqEndpoint;

  @Autowired
  private Gson gson;

  @Test
  public void testRoute() throws InterruptedException {

    /*
     * given
     */

    Cat biteyTabbyCat =
        Cat.builder().name("bitey").breed(Breed.TABBY).temperment(Temperment.FIESTY).build();
    Cat scratchyBritishBlueCat =
        biteyTabbyCat.toBuilder().name("scratchy").breed(Breed.BRITISH_BLUE).build();

    String biteyTabbyCatJson = gson.toJson(biteyTabbyCat);
    String scratchyBritishBlueJson = gson.toJson(scratchyBritishBlueCat);

    catsRusEndpoint.expectedBodiesReceived(biteyTabbyCatJson);
    dlqEndpoint.expectedBodiesReceived(scratchyBritishBlueJson);

    /*
     * When
     */

    testProducer.sendBody(biteyTabbyCatJson);
    testProducer.sendBody(scratchyBritishBlueJson);

    /*
     * then
     */

    catsRusEndpoint.assertIsSatisfied();

  }

}

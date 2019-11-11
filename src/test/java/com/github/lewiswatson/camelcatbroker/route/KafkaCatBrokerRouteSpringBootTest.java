package com.github.lewiswatson.camelcatbroker.route;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import org.apache.camel.CamelContext;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.camel.test.spring.EnableRouteCoverage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Awaitility;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import com.github.lewiswatson.camelcatbroker.CamelCatBrokerApplication;
import com.github.lewiswatson.camelcatbroker.model.Cat;
import com.github.lewiswatson.camelcatbroker.model.Cat.Breed;
import com.github.lewiswatson.camelcatbroker.model.Cat.Temperment;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest(classes = {CamelCatBrokerApplication.class})
@ActiveProfiles(profiles = "kafka")
@TestPropertySource(properties = "kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
@EnableRouteCoverage
@Slf4j
public class KafkaCatBrokerRouteSpringBootTest {

  @Produce(uri = "direct:test-cat-broker-sender")
  private ProducerTemplate testProducer;

  @Autowired
  private Gson gson;

  @Autowired
  private CamelContext camelContext;

  private KafkaMessageListenerContainer<String, String> container;

  private BlockingQueue<ConsumerRecord<String, String>> records;

  private static String[] topics = {"cat-broker", "cats-r-us", "dlq"};

  @ClassRule
  public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, topics);

  @Before
  public void setUp() throws Exception {
    // set up the Kafka consumer properties
    Map<String, Object> consumerProperties =
        KafkaTestUtils.consumerProps("sender", "false", embeddedKafka.getEmbeddedKafka());

    // create a Kafka consumer factory
    DefaultKafkaConsumerFactory<String, String> consumerFactory =
        new DefaultKafkaConsumerFactory<String, String>(consumerProperties);

    // set the topic that needs to be consumed
    ContainerProperties containerProperties =
        new ContainerProperties("cat-broker", "cats-r-us", "dlq");

    // create a Kafka MessageListenerContainer
    container = new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);

    // create a thread safe queue to store the received message
    records = new LinkedBlockingQueue<>();

    // setup a Kafka message listener
    container.setupMessageListener(new MessageListener<String, String>() {
      @Override
      public void onMessage(ConsumerRecord<String, String> record) {
        log.trace("test-listener received message='{}'", record.toString());
        records.add(record);
      }
    });

    // start the container and underlying message listener
    container.start();

    // wait until the container has the required number of assigned partitions
    ContainerTestUtils.waitForAssignment(container,
        embeddedKafka.getEmbeddedKafka().getPartitionsPerTopic() * topics.length);
  }

  @After
  public void tearDown() throws Exception {

    // avoid disgraceful shutdown of camel routes that use kafka,
    // which is what would happen kafka was to just disappear...
    camelContext.stop();

    // stop the container
    container.stop();
  }

  @Test
  public void testRoute() {

    /*
     * given
     */

    Cat bitey =
        Cat.builder().name("bitey").breed(Breed.TABBY).temperment(Temperment.FIESTY).build();

    // basically the same cat, but a british blue.
    Cat scratchy = bitey.toBuilder().name("scratchy").breed(Breed.BRITISH_BLUE).build();

    String biteyTabbyCatJson = gson.toJson(bitey);
    String scratchyBritishBlueJson = gson.toJson(scratchy);

    /*
     * when
     */

    testProducer.sendBody(biteyTabbyCatJson);
    testProducer.sendBody(scratchyBritishBlueJson);

    // wait until kafka has received two cat messages, and has routed them appropriately to two
    // cattery producers (four messages in total).
    Awaitility.await().until(() -> {
        log.trace("records size = {}", records.size());
        return records.size() == 4;
    });

    /*
     * then
     */

    Collection<ConsumerRecord<String, String>> actualRecords = new ArrayList<>();
    records.drainTo(actualRecords);
    assertThat(actualRecords).as("two messages were sent in, and two should come out, that makes 4")
        .hasSize(4);
    assertThat(actualRecords.stream().map(ConsumerRecord::topic).collect(Collectors.toList()))
        .as("one cat should go to cats-r-us and the other should go to the dead letter queue")
        .containsOnlyOnce("cats-r-us", "dlq");

  }

}

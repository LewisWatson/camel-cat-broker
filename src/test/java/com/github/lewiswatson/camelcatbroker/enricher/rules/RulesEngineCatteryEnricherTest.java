package com.github.lewiswatson.camelcatbroker.enricher.rules;

import static org.assertj.core.api.Assertions.assertThat;

import com.github.lewiswatson.camelcatbroker.enricher.DestinationCatteryEnricher;
import com.github.lewiswatson.camelcatbroker.enricher.rules.RulesEngineCatteryEnricherTest.TestContextConfiguration;
import com.github.lewiswatson.camelcatbroker.model.Cat;
import com.github.lewiswatson.camelcatbroker.model.Cat.Breed;
import com.github.lewiswatson.camelcatbroker.model.Cat.Temperment;
import com.github.lewiswatson.camelcatbroker.model.Cattery;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Offset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("rules-engine")
@ContextConfiguration(classes = {RulesEngineCatteryEnricherConfig.class,
    TestContextConfiguration.class})
@Slf4j
public class RulesEngineCatteryEnricherTest {

  @Autowired
  private DestinationCatteryEnricher destinationCatteryEnricher;

  @Test
  public void test() {

    /*
     * Given
     */

    Cat cat = Cat.builder().breed(Breed.BRITISH_BLUE).temperment(Temperment.CHILL).build();
    int sampleSize = 1000;
    Double expectedPercentSentToCatsRUs = 10d;
    Double expectedPercentSentToPawsForThought = 90d;
    Offset<Double> acceptablePercentageErrorMargin = Offset.offset(1.0);

    /*
     * When
     */

    Map<Cattery, Integer> catteryCount = new HashMap<>();
    for (int i = 0; i < sampleSize; i++) {
      Cattery chosenCattery = destinationCatteryEnricher.pickCattery(cat);
      catteryCount.put(chosenCattery,
          catteryCount.containsKey(chosenCattery) ? catteryCount.get(chosenCattery) + 1 : 1);
    }

    /*
     * Then
     */

    assertThat(catteryCount).as("we only expect cats to be sent to two catteries")
        .containsOnlyKeys(Cattery.CATS_R_US, Cattery.PAWS_FOR_THOUGHT);

    double actualPercentSentToCatsRUs =
        (double) catteryCount.get(Cattery.CATS_R_US) * 100 / sampleSize;
    double actualPercentSentToPawsForThought =
        (double) catteryCount.get(Cattery.PAWS_FOR_THOUGHT) * 100 / sampleSize;

    log.info("cats-r-us:{}%, paws-for-thought:{}%", actualPercentSentToCatsRUs,
        actualPercentSentToPawsForThought);

    assertThat(actualPercentSentToCatsRUs)
        .as("We expected {}% of cats to be sent to Cats-r-us", expectedPercentSentToCatsRUs)
        .isCloseTo(expectedPercentSentToCatsRUs, acceptablePercentageErrorMargin);
    assertThat(actualPercentSentToPawsForThought)
        .as("We expected {}% of cats to be sent to Paws-for-thought",
            expectedPercentSentToPawsForThought)
        .isCloseTo(expectedPercentSentToPawsForThought, acceptablePercentageErrorMargin);

  }


  @Configuration
  public static class TestContextConfiguration {

    @Bean
    public Gson gson() {
      return new Gson();
    }

    @Bean
    public CatRouterProperties properties() {
      return CatRouterProperties.builder().rulesPath("simple-rules.drl").build();
    }

  }
}
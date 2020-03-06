package com.github.lewiswatson.camelcatbroker.enricher.rules;

import com.github.lewiswatson.camelcatbroker.enricher.DestinationCatteryEnricher;
import com.github.lewiswatson.camelcatbroker.model.Cat;
import com.github.lewiswatson.camelcatbroker.model.Cattery;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

@Slf4j
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RulesEngineCatteryEnricher implements DestinationCatteryEnricher {

  private static final String GLOBAL_CATTERY_IDENTIFIER = "cattery";

  CatRouterProperties properties;
  Gson gson;
  KieContainer kieContainer;

  @Override
  public Cattery pickCattery(Cat cat) {

    KieSession kieSession = kieContainer.newKieSession();
    try {
      kieSession.setGlobal("log", log);
      kieSession.setGlobal("catteryWeights", new ArrayList<Pair<Cattery, Long>>());
      kieSession.insert(cat);
      kieSession.fireAllRules();
      List<Pair<Cattery, Double>> catteryWeights = (List<Pair<Cattery, Double>>) kieSession
          .getGlobal("catteryWeights");
      log.debug("Cattery Weights: {}", catteryWeights);
      EnumeratedDistribution<Cattery> enumeratedDistribution = new EnumeratedDistribution<Cattery>(
          catteryWeights);
      log.debug("Duplicated Mass Function: {}", enumeratedDistribution.getPmf());
      return enumeratedDistribution.sample();
    } finally {
      kieSession.dispose();
    }
  }

  @Override
  public void process(Exchange exchange) throws Exception {
    String bodyJson = exchange.getIn().getBody(String.class);
    Cat cat = gson.fromJson(bodyJson, Cat.class);
    Cattery cattery = pickCattery(cat);
    log.trace("picked cattery: {} for cat: {}", cattery, cat);
    exchange.getIn().setHeader(properties.getDestinationCatteryHeader(), cattery);
  }
}

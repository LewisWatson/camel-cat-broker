package com.github.lewiswatson.camelcatbroker.enricher;

import org.apache.camel.Exchange;
import com.github.lewiswatson.camelcatbroker.model.Cat;
import com.github.lewiswatson.camelcatbroker.model.Cat.Breed;
import com.github.lewiswatson.camelcatbroker.model.Cattery;
import com.github.lewiswatson.camelcatbroker.property.CatRouterProperties;
import com.google.gson.Gson;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Builder
public class TabbiesGoToCatsRusDestinationCatteryEnricher implements DestinationCatteryEnricher {

  private CatRouterProperties properties;
  private Gson gson;

  @Override
  public void process(Exchange exchange) throws Exception {
    String bodyJson = exchange.getIn().getBody(String.class);
    Cat cat = gson.fromJson(bodyJson, Cat.class);
    Cattery cattery = pickCattery(cat);
    log.trace("picked cattery: {} for cat: {}", cattery, cat);
    exchange.getIn().setHeader(properties.getDestinationCatteryHeader(), cattery);
  }

  @Override
  public Cattery pickCattery(Cat cat) {
    return cat.getBreed().equals(Breed.TABBY) ? Cattery.CATS_R_US : Cattery.UNWKNOWN;
  }

}

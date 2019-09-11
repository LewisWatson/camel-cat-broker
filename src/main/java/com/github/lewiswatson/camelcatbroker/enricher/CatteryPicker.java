package com.github.lewiswatson.camelcatbroker.enricher;

import com.github.lewiswatson.camelcatbroker.model.Cat;
import com.github.lewiswatson.camelcatbroker.model.Cattery;

/**
 * Picks a {@link Cattery} for a given {@link Cat}
 */
public interface CatteryPicker {
  
  Cattery pickCattery(Cat cat);

}

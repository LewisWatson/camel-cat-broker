package com.github.lewiswatson.camelcatbroker.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder=true)
public class Cat {

  public static final String UNKNOWN_CAT_NAME = "unknown";

  @NonNull
  @Builder.Default
  String name = UNKNOWN_CAT_NAME;

  @NonNull
  @Builder.Default
  Breed breed = Breed.UNKNOWN;

  @NonNull
  @Builder.Default
  Temperment temperment = Temperment.UNKNOWN;

  public enum Breed {
    UNKNOWN, TABBY, BRITISH_BLUE;
  }

  public enum Temperment {
    UNKNOWN, CHILL, FIESTY;
  }

}

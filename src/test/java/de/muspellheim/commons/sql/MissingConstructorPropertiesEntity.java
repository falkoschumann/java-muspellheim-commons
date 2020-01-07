/*
 * Muspellheim Commons
 * Copyright (c) 2019 Falko Schumann
 */

package de.muspellheim.commons.sql;

import java.beans.ConstructorProperties;
import lombok.Getter;
import lombok.Setter;

public class MissingConstructorPropertiesEntity {

  @Getter @Setter private final String text;
  @Getter @Setter private final int number;

  @ConstructorProperties({"text"})
  public MissingConstructorPropertiesEntity(String text, int number) {
    this.text = text;
    this.number = number;
  }
}

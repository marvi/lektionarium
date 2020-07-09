/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class HolyDay extends Day {
  private static final Logger LOGGER = Logger.getLogger( HolyDay.class.getName() );
  private Readings readings;
  private String theme;

  public HolyDay(String name, LocalDate date, List<Memorial> memorials, LiturgicalColor liturgicalColor,
                 Readings readings) {
    super(name, date, memorials, liturgicalColor);
    if(name == null || date == null || readings == null) {
      LOGGER.severe("Error when creating Holy day. Null value supplied.");
    }
    this.readings = readings;
    this.theme = readings.getTheme();
  }

  public boolean usesEasterSeries() {
    if (this.name.equals("Palmsöndagen")
      || this.name.equals("Skärtorsdagen")
      || this.name.equals("Långfredagen")
      || this.name.equals("Påsknatten")
      || this.name.equals("Påskdagen")) {
      return true;
    }
    return false;
  }

  public Readings getReadings() {
    return this.readings;
  }

  public String getTheme() {
    return theme;
  }

}

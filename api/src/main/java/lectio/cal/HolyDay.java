/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;
import java.util.Objects; // Added for Objects.requireNonNull
import java.util.logging.Logger;

public record HolyDay(Day day, Readings readings) implements LiturgicalDay {
  private static final Logger LOGGER = Logger.getLogger( HolyDay.class.getName() );

  public HolyDay {
    // Validate that day and readings are not null
    Objects.requireNonNull(day, "day cannot be null");
    Objects.requireNonNull(readings, "readings cannot be null");

  }

  // LiturgicalDay interface methods, delegated to Day
  @Override
  public String name() {
    return day.name();
  }

  @Override
  public java.time.LocalDate date() {
    return day.date();
  }

  @Override
  public java.util.List<Memorial> memorials() {
    return day.memorials();
  }

  @Override
  public LiturgicalColor liturgicalColor() {
    return day.liturgicalColor();
  }

  public String theme() {
    return this.readings.getTheme();
  }

  public boolean usesEasterSeries() {
    // Access name via the day component's accessor
    if (this.day.name().equals("Palmsöndagen")
      || this.day.name().equals("Skärtorsdagen")
      || this.day.name().equals("Långfredagen")
      || this.day.name().equals("Påsknatten")
      || this.day.name().equals("Påskdagen")) {
      return true;
    }
    return false;
  }

}

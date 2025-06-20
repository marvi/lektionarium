/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

// Removed LocalDate and List imports as they are encapsulated in Day record
// import java.time.LocalDate;
// import java.util.List;
import java.util.Objects; // Added for Objects.requireNonNull
import java.util.logging.Logger;

// Changed from class extending Day to a record.
// Day object is now a component. theme is derived via a method.
public record HolyDay(Day day, Readings readings) {
  private static final Logger LOGGER = Logger.getLogger( HolyDay.class.getName() );

  // Compact constructor for validation or other initialization logic
  public HolyDay {
    // The Day record itself would handle nulls for its components.
    // We must ensure readings is not null as theme() depends on it.
    // The original code logged a severe error if readings was null.
    // Records typically use Objects.requireNonNull for constructor validation.
    Objects.requireNonNull(day, "day cannot be null");
    Objects.requireNonNull(readings, "readings cannot be null");
    // The original code logged if name or date (now in day) were null.
    // This is implicitly handled by Day record or should be checked before creating Day.
    // If day.name() or day.date() could be null and that's an issue,
    // further checks could be added here, but Day record should enforce its own invariants.
  }

  // theme is now a method, derived from readings, as per interpretation of requirements
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

  // Getters for 'day' and 'readings' are auto-generated by the record.
  // getTheme() is replaced by theme() method above.
  // getReadings() is replaced by readings() accessor.
}

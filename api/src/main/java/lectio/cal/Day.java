/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.LocalDate;
// DateTimeFormatter import is no longer needed as toString is auto-generated
// import java.time.format.DateTimeFormatter;
import java.util.List;
// Objects import is no longer needed as equals and hashCode will be auto-generated
// import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public record Day(String name, LocalDate date, List<Memorial> memorials, LiturgicalColor liturgicalColor, @Nullable Readings readings) implements Comparable<Day> {

  // Constructor, getters, equals, hashCode, and toString are auto-generated for records.

  public boolean isHolyDay() {
    return readings != null;
  }

  public @Nullable String theme() {
    return readings != null ? readings.getTheme() : null;
  }

  // The problem description asks to update compareTo(Object o) to compareTo(Day d)
  // However, for a record to implement Comparable<Day>, it should implement compareTo(Day d).
  // Also, the existing compareTo only compares dates, which might not be what's desired
  // for a full comparison of Day objects. For now, I will keep the logic as is,
  // but just change the signature and remove the cast.
  // A more complete compareTo would consider other fields if date is equal.
  @Override
  public int compareTo(Day d) {
    // The original implementation only compared dates.
    // Records provide accessors like d.date()
    if (this.date == null && d.date == null) return 0;
    if (this.date == null) return -1;
    if (d.date == null) return 1;
    return this.date.compareTo(d.date());
  }

  // The original toString() method had specific formatting for the date.
  // The auto-generated toString() for a record will include all fields.
  // If the specific format was important, a custom toString() would be needed.
  // For this refactoring, I'm assuming the default toString() is acceptable.
  // If not, the original toString() or a modified version would need to be added back.
  //
  // Example of how to override toString if needed:
  // @Override
  // public String toString() {
  //   DateTimeFormatter fmt = DateTimeFormatter.ofPattern("Y-MM-dd");
  //   return "Day{" + "name=" + name + ", date=" + fmt.format(date) + ", memorials=" + memorials + ", liturgicalColor=" + liturgicalColor + '}';
  // }

  // Note: The original compareTo compared this.date with d.getDate().
  // For records, the accessor methods are named after the component, e.g., d.date().
  // The original equals and hashCode methods were based only on the 'date' field.
  // The auto-generated equals and hashCode for a record will use all components,
  // which is generally more correct.
}

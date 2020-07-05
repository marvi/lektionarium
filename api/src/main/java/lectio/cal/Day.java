/*
 * Copyright (c) 2006, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class Day {

  String name;
  LocalDate date;
  List<Memorial> memorials;
  LiturgicalColor liturgicalColor;

  public Day(String name, LocalDate date, List<Memorial> memorials, LiturgicalColor liturgicalColor) {
    this.name = name;
    this.date = date;
    this.memorials = memorials;
    this.liturgicalColor = liturgicalColor;
  }

  public LocalDate getDate() {
    return this.date;
  }

  public String getName() {
    return this.name;
  }
  public List<Memorial> getMemorials() {
    return this.memorials;
  }

  public LiturgicalColor getLiturgicalColor() {
    return this.liturgicalColor;
  }

  public int compareTo(Object o) {
    Day d = (Day) o;
    return this.date.compareTo(d.getDate());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Day other = (Day) obj;
    if (!Objects.equals(this.date, other.date)) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 23 * hash + (this.date != null ? this.date.hashCode() : 0);
    return hash;
  }

  @Override
  public String toString() {
    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("Y-MM-dd");
    return "Day{" + "name=" + name + ", date=" + fmt.format(date) + '}';
  }
}

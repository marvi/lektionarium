/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;


import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * This class acts as a LiturgicalYear cache for LiturgicalYear instances so that
 * all the date calculations doesn't have to be repeated.
 * It also has a few methods to "walk" the calendar back and forth,
 * and to get the current Day.
 *
 * @author marvi
 */
public class LiturgicalYearFactory {

  private static final Logger LOGGER = Logger.getLogger( LiturgicalYearFactory.class.getName() );

  /**
   * Concurrent hash map, able to handle multiple threads
   */
  private ConcurrentHashMap<Integer, LiturgicalYear> liturgicalYearCache = new ConcurrentHashMap<>();

  public LiturgicalYear getYear(int year) {
    return liturgicalYearCache.computeIfAbsent(year, y -> new LiturgicalYear(y));
  }

  /**
   * Find the current holy day. In the liturgical year, we "live" in the last holy day until the next.
   * So Tuesday after will show second day Easter.
   * Todo: 1. handle days that don't continue, like Ash Wednesday
   *       2. handle switching current day on saturday evening for Sundays
   * @param d The current date
   * @return The current holy day
   */
  @NotNull
  public Day getCurrentDay(LocalDate d) {
    Day day = null;
    SortedMap<LocalDate, Day> days = getYear(d.getYear()).getDaysOfYear();
    days.putAll(getYear(d.getYear() + 1).getDaysOfYear());
    while (day == null) {
      if(days.containsKey(d)) {
        day = days.get(d);
      }
      else {
        d = d.minusDays(1);
      }
    }
    return day;
  }

  /**
   * From a given date, find the next holy day.
   * @param d The day to start from
   * @return The next holy day
   */
  @NotNull
  public Day getNextDay(LocalDate d) {
    Day day = null;
    d = d.plusDays(1);
    SortedMap<LocalDate, Day> days = getYear(d.getYear()).getDaysOfYear();
    days.putAll(getYear(d.getYear() + 1).getDaysOfYear());
    while (day == null) {
      if(days.containsKey(d)) {
        day = days.get(d);
      }
      else {
        d = d.plusDays(1);
      }
    }
    return day;
  }

  /**
   * From a specified day, go back to the current holy day (on Tuesday go to the previous Sunday)
   * Then from that holy day go back until we find the previous holy day.
   *
   * @param d The day to start from
   * @return The previous holy day
   */
  @NotNull
  public Day getPreviousDay(LocalDate d) {
    Day day = null;
    d = getCurrentDay(d).date;
    d = d.minusDays(1);
    SortedMap<LocalDate, Day> days = getYear(d.getYear()).getDaysOfYear();
    days.putAll(getYear(d.getYear() - 1).getDaysOfYear());
    days.putAll(getYear(d.getYear() + 1).getDaysOfYear());
    while (day == null) {
      if(days.containsKey(d)) {
        day = days.get(d);
      }
      else {
        d = d.minusDays(1);
      }
    }
    return day;
  }

  @NotNull
  public LiturgicalYear getDaysOfLiturgicalYear(int year) {
    return getYear(year);
  }

  /**
   * Liturgical year != calendar year. This method returns all holy days for a calendar year
   * @param year The calendar year to fetch holy days for
   * @return All holy days for a calendar year
   */
  @NotNull
  public Map<LocalDate, Day> getDaysOfCalendarYear(int year) {
    // Fetch current and next liturgical year (to get Advent and Christmas)
    SortedMap<LocalDate, Day> daysOfYear = getYear(year).getDaysOfYear();
    daysOfYear.putAll(getYear(year + 1).getDaysOfYear());

    // Filter map for days in supplied year
    return daysOfYear.entrySet().stream()
      .filter(map -> map.getKey().getYear() == year)
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Find the Liturgical year for a date. Handle edge cases like first of advent.
   *
   * If a date is after first of Advent and before New Years day the liturgical
   * year is calendar year + 1. Else it's equal to calendar year
   *
   *
   * @param d A date
   * @return The Liturgical year for this date
   */
  public LiturgicalYear getLiturgicalYear(LocalDate d) {
    Map<LocalDate, Day> daysOfCalendarYear = this.getDaysOfCalendarYear(d.getYear());
    Optional<Day> match = daysOfCalendarYear.values().parallelStream()
      .filter(day -> day.name.equals("Första söndagen i advent")).findAny();
    if(match.isPresent()) {
      LocalDate firstAdv = match.get().date;
      if(d.equals(firstAdv) ||
        (d.isAfter(firstAdv) && (d.getYear() == firstAdv.getYear()))) {
        return this.getYear(d.getYear() + 1);
      }
      else {
        return this.getYear(d.getYear());
      }
    }
    else {
      LOGGER.severe("Could not find out liturgical year for date  "  + d);
      return null;
    }
  }

  @NotNull
  public Map<LocalDate, Day> getCalendarMonth(int year, int month) {
    Map<LocalDate, Day> daysOfYear = getDaysOfCalendarYear(year);

    // Filter map for days in supplied month
    return daysOfYear.entrySet().stream()
      .filter(map -> map.getKey().getMonthValue() == month)
      .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

}



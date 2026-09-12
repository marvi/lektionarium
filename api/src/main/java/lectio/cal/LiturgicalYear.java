/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Ett kyrkoår i Svenska kyrkans tradition.
 * <p>
 * Kalenderår och kyrkoår sammanfaller inte. Kyrkoåret börjar första söndagen i
 * advent under föregående kalenderår. Året som används i den här klassen är året
 * för kyrkoårets påskdag: kyrkoåret 2013 börjar alltså 2012-12-02.
 * <p>
 * Instanser är oföränderliga och kan delas mellan trådar. Skapa dem via
 * {@link LiturgicalYearFactory}, som återanvänder redan uträknade år.
 * <p>
 * Inspiration från <a href="http://www.lysator.liu.se/alma/alma.cgi">alma</a>.
 * Se även <a href="https://www.svenskakyrkan.se/filer/KO_1_jan_2020.pdf">Svenska
 * kyrkans kyrkoordning</a>.
 *
 * @author marvi
 */
public class LiturgicalYear {

  /** Första året evangelieboken från 2003 års kyrkohandbok täcker fullt ut. */
  public static final int FIRST_SUPPORTED_YEAR = 2004;

  private final int year;
  private final LocalDate easterDay;
  private final int readingCycle;
  private final int easterSeries;
  private final ReadingCycles readingCycles;
  private final SortedMap<LocalDate, Day> daysOfYear = new TreeMap<>();

  /**
   * @param year kyrkoåret, måste vara {@value #FIRST_SUPPORTED_YEAR} eller senare
   * @throws IllegalArgumentException om året ligger före {@value #FIRST_SUPPORTED_YEAR}
   */
  public LiturgicalYear(int year) {
    this(year, LectioRepository.getLectio());
  }

  /**
   * @param year          kyrkoåret
   * @param readingCycles evangelieboken att hämta texterna ur
   * @throws IllegalArgumentException om året ligger före {@value #FIRST_SUPPORTED_YEAR}
   */
  public LiturgicalYear(int year, ReadingCycles readingCycles) {
    if (year < FIRST_SUPPORTED_YEAR) {
      throw new IllegalArgumentException(
        "Endast år från och med " + FIRST_SUPPORTED_YEAR + " stöds, fick " + year);
    }
    this.year = year;
    this.readingCycle = getReadingCycle(year);
    this.easterSeries = getEasterSeries(year);
    this.readingCycles = Objects.requireNonNull(readingCycles, "readingCycles");
    Anchors anchors = Anchors.of(year);
    this.easterDay = anchors.easter();
    ChurchYearRules.layOut(anchors).forEach((date, placement) ->
      daysOfYear.put(date, makeDay(placement.name(), date, placement.color())));
  }

  private Day makeDay(String name, LocalDate date, LiturgicalColor color) {
    return readingsFor(name)
      .<Day>map(readings -> new HolyDay(name, date, color, readings))
      .orElseGet(() -> new OrdinaryDay(name, date, color));
  }

  /** Stilla veckan och påsken följer påskserien, övriga dagar läsningsserien. */
  private Optional<Readings> readingsFor(String name) {
    int cycle = HolyDay.usesEasterSeries(name) ? easterSeries : readingCycle;
    return readingCycles.readingsFor(name, cycle);
  }

  /**
   * Kyrkoårets dagar, ordnade efter datum.
   *
   * @return en oföränderlig vy med datum som nyckel
   */
  public SortedMap<LocalDate, Day> getDaysOfYear() {
    return Collections.unmodifiableSortedMap(daysOfYear);
  }

  /**
   * @param name dagens namn i kyrkoåret
   * @return dagen, om den finns detta kyrkoår och har egna texter
   */
  public Optional<HolyDay> findHolyDayByName(String name) {
    return daysOfYear.values().stream()
      .filter(day -> day.name().equals(name))
      .filter(HolyDay.class::isInstance)
      .map(HolyDay.class::cast)
      .findFirst();
  }

  /** @return läsningsserien, 1-3, för det här kyrkoåret */
  public int getReadingCycle() {
    return readingCycle;
  }

  /**
   * Läsningsserien löper i treårscykler från 2003.
   *
   * @param year ett kyrkoår
   * @return serien, 1-3, eller 0 för år före 1986
   */
  public static int getReadingCycle(int year) {
    if (year < 1986) {
      return 0;
    } else if (year < 2003) {
      return (year - 1986) % 3 + 1;
    }
    return (year - 2003) % 3 + 1;
  }

  /** @return påskserien, 1-4, för det här kyrkoåret */
  public int getEasterSeries() {
    return easterSeries;
  }

  /**
   * Påskserien löper i fyraårscykler från 2004.
   *
   * @param year ett kyrkoår
   * @return serien, 1-4, eller 0 för år före 2004
   */
  public static int getEasterSeries(int year) {
    if (year < FIRST_SUPPORTED_YEAR) {
      return 0;
    }
    return (year - FIRST_SUPPORTED_YEAR) % 4 + 1;
  }

  /** @return kyrkoåret, räknat efter sin påskdag */
  public int getYear() {
    return year;
  }

  /** @return påskdagen detta kyrkoår */
  public LocalDate getEasterDay() {
    return easterDay;
  }

  /**
   * @param dayOfWeek veckodagen att leta efter
   * @param from      datumet att räkna från, exklusive
   * @return nästa datum efter {@code from} som infaller på veckodagen
   */
  protected static LocalDate nextWeekdayOfType(DayOfWeek dayOfWeek, LocalDate from) {
    return from.with(TemporalAdjusters.next(dayOfWeek));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("LiturgicalYear{year=").append(year).append('\n');
    daysOfYear.values().forEach(day -> sb.append("  ").append(day).append('\n'));
    return sb.append('}').toString();
  }
}

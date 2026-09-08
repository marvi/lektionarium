/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.format;

import lectio.cal.Day;
import lectio.cal.LiturgicalYearFactory;
import lectio.cal.Reading;
import lectio.cal.Readings;

import java.util.Collection;
import java.util.Optional;

/**
 * Kyrkoåret som semikolonseparerad CSV, avsedd för kalkylprogram.
 *
 * <pre>
 * Datum;Namn;Tema;GT;Epistel;Evangelium;Psaltarpsalm;Alternativ text
 * 2020-01-01;Nyårsdagen;I Jesu namn;Klag 3:22-26;Apg 10:42-43;Joh 2:23-25;Ps 121;
 * </pre>
 *
 * Fält som innehåller semikolon, citattecken eller radbrytning citeras enligt
 * RFC 4180, med dubblade citattecken inuti.
 *
 * @author marvi
 */
public final class CsvFormat {

  private static final String HEADER =
    "Datum;Namn;Tema;GT;Epistel;Evangelium;Psaltarpsalm;Alternativ text";

  private CsvFormat() {
  }

  /**
   * @param year kyrkoåret
   * @return kalendern som CSV
   */
  public static String forLiturgicalYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.LITURGICAL, year);
  }

  /**
   * @param year kalenderåret
   * @return kalendern som CSV
   */
  public static String forCalendarYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.CALENDAR, year);
  }

  /**
   * @param factory kalendern att hämta dagarna ur
   * @param basis   om årtalet syftar på kyrkoår eller kalenderår
   * @param year    årtalet
   * @return kalendern som CSV
   */
  public static String forYear(LiturgicalYearFactory factory, CalendarBasis basis, int year) {
    return forDays(basis.daysOf(factory, year));
  }

  /**
   * @param days dagarna att skriva ut, i den ordning de ska stå
   * @return dagarna som CSV, med rubrikrad
   */
  public static String forDays(Collection<Day> days) {
    StringBuilder out = new StringBuilder(HEADER).append('\n');
    for (Day day : days) {
      Optional<Readings> readings = day.findReadings();
      appendField(out, day.date().toString());
      appendField(out, day.name());
      appendField(out, readings.map(Readings::theme).orElse(""));
      appendField(out, ref(readings.map(Readings::ot)));
      appendField(out, ref(readings.map(Readings::ep)));
      appendField(out, ref(readings.map(Readings::go)));
      appendField(out, ref(readings.map(Readings::ps)));
      out.append(escape(ref(readings.map(Readings::alt)))).append('\n');
    }
    return out.toString();
  }

  private static String ref(Optional<Reading> reading) {
    return reading.map(Reading::sweRef).orElse("");
  }

  private static void appendField(StringBuilder out, String value) {
    out.append(escape(value)).append(';');
  }

  private static String escape(String value) {
    if (value.indexOf(';') < 0 && value.indexOf('"') < 0
      && value.indexOf('\n') < 0 && value.indexOf('\r') < 0) {
      return value;
    }
    return '"' + value.replace("\"", "\"\"") + '"';
  }
}

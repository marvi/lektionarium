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

/**
 * Kyrkoåret som läsbar text.
 *
 * <pre>
 * 2026-04-05 Påskdagen
 *   Kristus är uppstånden
 *   Gammaltestamentlig text: Jes 25:6-9
 *   ...
 * </pre>
 *
 * @author marvi
 */
public final class TextFormat {

  private TextFormat() {
  }

  /**
   * @param year kyrkoåret
   * @return kalendern som text
   */
  public static String forLiturgicalYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.LITURGICAL, year);
  }

  /**
   * @param year kalenderåret
   * @return kalendern som text
   */
  public static String forCalendarYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.CALENDAR, year);
  }

  /**
   * @param factory kalendern att hämta dagarna ur
   * @param basis   om årtalet syftar på kyrkoår eller kalenderår
   * @param year    årtalet
   * @return kalendern som text
   */
  public static String forYear(LiturgicalYearFactory factory, CalendarBasis basis, int year) {
    return forDays(basis.daysOf(factory, year));
  }

  /**
   * @param days dagarna att skriva ut, i den ordning de ska stå
   * @return dagarna som text
   */
  public static String forDays(Collection<Day> days) {
    StringBuilder out = new StringBuilder();
    for (Day day : days) {
      out.append(day.date()).append(' ').append(day.name()).append('\n');
      day.findReadings().ifPresent(readings -> appendReadings(out, readings));
    }
    return out.toString();
  }

  private static void appendReadings(StringBuilder out, Readings readings) {
    out.append("  ").append(readings.theme()).append('\n');
    appendReading(out, "Gammaltestamentlig text", readings.ot());
    appendReading(out, "Epistel", readings.ep());
    appendReading(out, "Evangelium", readings.go());
    appendReading(out, "Psaltarpsalm", readings.ps());
    appendReading(out, "Alternativ text", readings.alt());
    out.append('\n');
  }

  private static void appendReading(StringBuilder out, String label, Reading reading) {
    if (reading != null) {
      out.append("  ").append(label).append(": ").append(reading.sweRef()).append('\n');
    }
  }
}

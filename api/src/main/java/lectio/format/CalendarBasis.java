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

import java.time.LocalDate;
import java.util.Collection;
import java.util.SortedMap;

/**
 * Vilket slags år ett årtal syftar på.
 * <p>
 * Kyrkoår och kalenderår sammanfaller inte: kyrkoåret 2026 börjar första
 * söndagen i advent 2025. Vilket man vill ha beror på vad kalendern ska
 * användas till, så biblioteket gissar inte.
 *
 * @author marvi
 */
public enum CalendarBasis {

  /** Kyrkoåret, från första söndagen i advent till domssöndagen. */
  LITURGICAL {
    @Override
    public SortedMap<LocalDate, Day> days(LiturgicalYearFactory factory, int year) {
      return factory.getYear(year).getDaysOfYear();
    }
  },

  /** Kalenderåret, från 1 januari till 31 december. */
  CALENDAR {
    @Override
    public SortedMap<LocalDate, Day> days(LiturgicalYearFactory factory, int year) {
      return factory.getDaysOfCalendarYear(year);
    }
  };

  /**
   * @param factory kalendern att hämta dagarna ur
   * @param year    årtalet, tolkat enligt den här konstanten
   * @return dagarna för året, ordnade efter datum
   */
  public abstract SortedMap<LocalDate, Day> days(LiturgicalYearFactory factory, int year);

  /**
   * @param year årtalet
   * @return ett namn på kalendern som säger vilket slags år det rör sig om
   */
  public String calendarName(int year) {
    return this == LITURGICAL ? "Kyrkoåret " + year : "Kyrkoåret under " + year;
  }

  Collection<Day> daysOf(LiturgicalYearFactory factory, int year) {
    return days(factory, year).values();
  }
}

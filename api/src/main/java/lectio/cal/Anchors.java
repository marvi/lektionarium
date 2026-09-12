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

/**
 * Det som kyrkoårets övriga dagar räknas från.
 *
 * @param year        kyrkoåret, räknat efter sin påskdag
 * @param easter      påskdagen
 * @param firstAdvent första söndagen i advent, som inleder kyrkoåret året innan
 * @param nextAdvent  första söndagen i advent som inleder nästa kyrkoår
 */
record Anchors(int year, LocalDate easter, LocalDate firstAdvent, LocalDate nextAdvent) {

  static Anchors of(int year) {
    return new Anchors(year, CalculateEaster.forYear(year), adventOf(year - 1), adventOf(year));
  }

  /** Första söndagen i advent: söndagen 27 november-3 december. */
  static LocalDate adventOf(int calendarYear) {
    return LocalDate.of(calendarYear, 11, 27).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
  }
}

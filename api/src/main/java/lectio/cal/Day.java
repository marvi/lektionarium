/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * En dag i kyrkoåret.
 * <p>
 * En dag är antingen en {@link HolyDay} med egna texter ur evangelieboken,
 * eller en {@link OrdinaryDay} utan. Typen är förseglad, så en {@code switch}
 * över de två fallen är uttömmande:
 * <pre>{@code
 * String text = switch (day) {
 *   case HolyDay h -> h.name() + ": " + h.theme();
 *   case OrdinaryDay o -> o.name();
 * };
 * }</pre>
 *
 * @author marvi
 */
public sealed interface Day extends Comparable<Day> permits OrdinaryDay, HolyDay {

  /** Dagens namn i kyrkoåret, t.ex. "Första söndagen i advent". */
  String name();

  /** Det datum dagen infaller detta år. */
  LocalDate date();

  /** Liturgisk färg för dagen. */
  LiturgicalColor color();

  /** Dagens minnesdagar. Aldrig null, men oftast tom. */
  List<Memorial> memorials();

  /**
   * Läsningarna för dagen, om den har några.
   * <p>
   * Bekvämlighetsmetod för den som inte vill mönstermatcha på {@link HolyDay}.
   *
   * @return dagens läsningar, eller tomt för en {@link OrdinaryDay}
   */
  default Optional<Readings> findReadings() {
    return this instanceof HolyDay holyDay ? Optional.of(holyDay.readings()) : Optional.empty();
  }

  /**
   * Ordnar dagar kronologiskt.
   * <p>
   * Observera att ordningen inte är förenlig med {@code equals}: två dagar med
   * samma datum jämför lika utan att vara samma dag.
   */
  @Override
  default int compareTo(Day other) {
    return date().compareTo(other.date());
  }
}

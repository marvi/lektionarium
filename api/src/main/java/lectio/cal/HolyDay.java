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
import java.util.Objects;
import java.util.Set;

/**
 * En dag i kyrkoåret med egna texter i evangelieboken.
 *
 * @author marvi
 */
public record HolyDay(String name, LocalDate date, LiturgicalColor color,
                      List<Memorial> memorials, Readings readings) implements Day {

  /**
   * Dagarna i stilla veckan och påsken följer påskserien (fyra år) i stället för
   * den treåriga läsningsserien.
   */
  private static final Set<String> EASTER_SERIES_DAYS = Set.of(
    "Palmsöndagen", "Skärtorsdagen", "Långfredagen", "Påsknatten", "Påskdagen");

  public HolyDay {
    Objects.requireNonNull(name, "name");
    Objects.requireNonNull(date, "date");
    Objects.requireNonNull(color, "color");
    Objects.requireNonNull(readings, "readings");
    memorials = List.copyOf(memorials);
  }

  public HolyDay(String name, LocalDate date, LiturgicalColor color, Readings readings) {
    this(name, date, color, List.of(), readings);
  }

  /** Dagens tema, t.ex. "Ett nådens år". */
  public String theme() {
    return readings.theme();
  }

  /** @return true om dagen hämtar sina texter ur påskserien */
  public boolean usesEasterSeries() {
    return usesEasterSeries(name);
  }

  /**
   * @param dayName namnet på en dag i kyrkoåret
   * @return true om dagen hämtar sina texter ur påskserien i stället för läsningsserien
   */
  public static boolean usesEasterSeries(String dayName) {
    return EASTER_SERIES_DAYS.contains(dayName);
  }
}

/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

/**
 * En läsningsserie i evangelieboken.
 * <p>
 * Läsningsserien löper i treårscykler från 2003 och styr texterna för de
 * flesta dagar. Påskserien löper i fyraårscykler från 2004 och styr
 * texterna för palmsöndagen, stilla veckan och påskdagen, se
 * {@link HolyDay#usesEasterSeries(String)}.
 *
 * @author marvi
 */
public enum Cycle {
  FIRST(1),
  SECOND(2),
  THIRD(3),
  /** Finns bara i påskserien. */
  FOURTH(4);

  private final int value;

  Cycle(int value) {
    this.value = value;
  }

  /** @return seriens nummer, 1-4 */
  public int value() {
    return value;
  }

  /**
   * @param value seriens nummer, 1-4
   * @return motsvarande serie
   * @throws IllegalArgumentException om numret ligger utanför 1-4
   */
  public static Cycle of(int value) {
    for (Cycle cycle : values()) {
      if (cycle.value == value) {
        return cycle;
      }
    }
    throw new IllegalArgumentException("Okänd läsningsserie: " + value);
  }

  /**
   * Läsningsserien för ett kyrkoår, i treårscykler från 2003.
   *
   * @param year kyrkoåret, {@value LiturgicalYear#FIRST_SUPPORTED_YEAR} eller senare
   * @return serien, 1-3
   * @throws IllegalArgumentException om året ligger före {@value LiturgicalYear#FIRST_SUPPORTED_YEAR}
   */
  public static Cycle readingCycleOf(int year) {
    requireSupported(year);
    return of((year - 2003) % 3 + 1);
  }

  /**
   * Påskserien för ett kyrkoår, i fyraårscykler från 2004.
   *
   * @param year kyrkoåret, {@value LiturgicalYear#FIRST_SUPPORTED_YEAR} eller senare
   * @return serien, 1-4
   * @throws IllegalArgumentException om året ligger före {@value LiturgicalYear#FIRST_SUPPORTED_YEAR}
   */
  public static Cycle easterSeriesOf(int year) {
    requireSupported(year);
    return of((year - LiturgicalYear.FIRST_SUPPORTED_YEAR) % 4 + 1);
  }

  private static void requireSupported(int year) {
    if (year < LiturgicalYear.FIRST_SUPPORTED_YEAR) {
      throw new IllegalArgumentException(
        "Endast år från och med " + LiturgicalYear.FIRST_SUPPORTED_YEAR + " stöds, fick " + year);
    }
  }
}

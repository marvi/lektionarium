/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Evangelieboken: läsningarna för varje dag i kyrkoåret, per läsningsserie.
 * <p>
 * De flesta dagar har tre serier. Dagarna i stilla veckan och påsken har i
 * stället fyra, se {@link HolyDay#usesEasterSeries(String)}.
 *
 * @author marvi
 */
public final class ReadingCycles {

  private final Map<String, Map<Cycle, Readings>> byDay = new HashMap<>();

  /** Paketprivat: instanser byggs av {@link LectioRepository}. */
  ReadingCycles() {
  }

  void add(String holyDay, Cycle cycle, Readings readings) {
    byDay.computeIfAbsent(holyDay, key -> new EnumMap<>(Cycle.class)).putIfAbsent(cycle, readings);
  }

  /**
   * @param holyDay dagens namn i kyrkoåret
   * @param cycle   läsningsserie
   * @return läsningarna, eller tomt om dagen saknar texter i den serien
   */
  public Optional<Readings> readingsFor(String holyDay, Cycle cycle) {
    return Optional.ofNullable(byDay.getOrDefault(holyDay, Map.of()).get(cycle));
  }

  /**
   * @param holyDay dagens namn i kyrkoåret
   * @param cycle   läsningsserie som siffra, 1-4
   * @return läsningarna, eller tomt om dagen saknar texter i den serien
   * @throws IllegalArgumentException om serien inte är 1-4
   */
  public Optional<Readings> readingsFor(String holyDay, int cycle) {
    return readingsFor(holyDay, Cycle.of(cycle));
  }

  /** @return true om dagen alls förekommer i evangelieboken */
  public boolean hasReadings(String holyDay) {
    return byDay.containsKey(holyDay);
  }

  /** @return namnen på alla dagar som har texter */
  public Set<String> dayNames() {
    return Collections.unmodifiableSet(byDay.keySet());
  }

  /**
   * Om den inlästa evangelieboken bär bibeltext eller bara hänvisningar.
   * <p>
   * Filen som följer med biblioteket har texten bortstrippad av
   * upphovsrättsskäl. En driftsättning som har rätt att visa text pekar ut en
   * egen fil, och kan använda det här för att veta vilken sorts fil som lästs.
   *
   * @return true om någon dag i evangelieboken har bibeltext
   */
  public boolean containsBibleText() {
    return byDay.values().stream()
      .flatMap(cycles -> cycles.values().stream())
      .anyMatch(Readings::hasText);
  }

  /** En läsningsserie i evangelieboken. */
  public enum Cycle {
    FIRST(1),
    SECOND(2),
    THIRD(3),
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
  }
}

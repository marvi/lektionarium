/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import lectio.cal.ChurchYearRules.Feast;
import lectio.cal.ChurchYearRules.Placement;
import lectio.cal.ChurchYearRules.Rank;
import lectio.cal.ChurchYearRules.Series;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marvi
 */
class ChurchYearRulesTest {

  /** Dagar i kyrkoåret som saknar egna texter i evangelieboken. */
  private static final Set<String> WITHOUT_READINGS =
    Set.of("Måndag i Stilla veckan", "Tisdag i Stilla veckan", "Onsdag i Stilla veckan");

  @Test
  @DisplayName("ordningen i tabellen saknar betydelse")
  void ordningenSaknarBetydelse() {
    List<Feast> feasts = new ArrayList<>(ChurchYearRules.FEASTS);
    Collections.reverse(feasts);
    List<Series> series = new ArrayList<>(ChurchYearRules.SERIES);
    Collections.reverse(series);
    for (int year = 2004; year <= 2060; year++) {
      Anchors anchors = Anchors.of(year);
      assertEquals(ChurchYearRules.layOut(anchors), ChurchYearRules.layOut(anchors, feasts, series),
        "kyrkoåret " + year);
    }
  }

  @Test
  @DisplayName("lika rang på samma datum är ett fel i tabellen")
  void likaRangArEttFel() {
    List<Feast> feasts = List.of(
      new Feast("Juldagen", LiturgicalColor.WHITE, Rank.FEAST, DateRule.fixed(12, 25)),
      new Feast("Dubblett", LiturgicalColor.WHITE, Rank.FEAST, DateRule.fixed(12, 25)));
    IllegalStateException e = assertThrows(IllegalStateException.class,
      () -> ChurchYearRules.layOut(Anchors.of(2026), feasts, List.of()));
    assertTrue(e.getMessage().contains("Juldagen"), e.getMessage());
    assertTrue(e.getMessage().contains("Dubblett"), e.getMessage());
  }

  @Test
  @DisplayName("varje dag i tabellen finns i evangelieboken, och varje dag i evangelieboken läggs ut")
  void tabellenOchEvangeliebokenStammerOverens() {
    Set<String> lectio = LectioRepository.getLectio().dayNames();
    Set<String> laidOut = new TreeSet<>();
    for (int year = 2004; year <= 2050; year++) {
      ChurchYearRules.layOut(Anchors.of(year)).values().stream().map(Placement::name).forEach(laidOut::add);
    }

    Set<String> unknown = new TreeSet<>(laidOut);
    unknown.removeAll(lectio);
    unknown.removeAll(WITHOUT_READINGS);
    assertTrue(unknown.isEmpty(), "dagar utan texter i evangelieboken: " + unknown);

    Set<String> neverPlaced = new TreeSet<>(lectio);
    neverPlaced.removeAll(laidOut);
    assertTrue(neverPlaced.isEmpty(), "dagar i evangelieboken som aldrig läggs ut: " + neverPlaced);
  }

  @Test
  void varjeDagHarEttNamnOchEttDatum() {
    SortedMap<LocalDate, Placement> days = ChurchYearRules.layOut(Anchors.of(2026));
    Set<String> names = new HashSet<>();
    days.values().forEach(p -> assertTrue(names.add(p.name()), p.name() + " förekommer två gånger"));
    assertEquals(LocalDate.of(2025, 11, 30), days.firstKey());
    assertEquals(LocalDate.of(2026, 11, 22), days.lastKey(), "Domssöndagen");
  }
}

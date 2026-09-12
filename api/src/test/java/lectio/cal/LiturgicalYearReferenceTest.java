/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Jämför utläggningen av kyrkoåren 2027-2041 mot ett sparat facit.
 * <p>
 * {@code kyrkoar-facit.txt} skrevs av den här klassen 2026-09-12 och stämdes
 * då av, dag för dag, mot Alma (Lysators C-implementation av den svenska
 * almanackan) utan någon avvikelse. Testet låser fast varje dag, datum och
 * färg och är skyddsnätet vid omskrivning av {@link LiturgicalYear}.
 * Reglerna bakom utläggningen testas var för sig i {@link LiturgicalYearTest}.
 *
 * @author marvi
 */
class LiturgicalYearReferenceTest {

  private static final int FIRST_YEAR = 2027;
  private static final int LAST_YEAR = 2041;

  @Test
  void stammerMedFacit() {
    List<String> actual = new ArrayList<>();
    for (int year = FIRST_YEAR; year <= LAST_YEAR; year++) {
      int y = year;
      new LiturgicalYear(y).getDaysOfYear().values().forEach(day ->
        actual.add(y + "\t" + day.date() + "\t" + day.name() + "\t" + day.color()));
    }
    List<String> expected = readFacit();
    List<String> diff = new ArrayList<>();
    for (int i = 0; i < Math.max(expected.size(), actual.size()) && diff.size() < 10; i++) {
      String e = i < expected.size() ? expected.get(i) : "<saknas>";
      String a = i < actual.size() ? actual.get(i) : "<saknas>";
      if (!e.equals(a)) {
        diff.add("rad " + (i + 1) + "\n  facit: " + e + "\n  kod:   " + a);
      }
    }
    assertTrue(diff.isEmpty(), () -> "utläggningen avviker från facit, första skillnaderna:\n" + String.join("\n", diff));
  }

  private static List<String> readFacit() {
    try {
      Path path = Path.of(LiturgicalYearReferenceTest.class.getResource("/kyrkoar-facit.txt").toURI());
      return Files.readAllLines(path).stream()
        .filter(line -> !line.isBlank() && !line.startsWith("#"))
        .toList();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (URISyntaxException e) {
      throw new IllegalStateException(e);
    }
  }
}

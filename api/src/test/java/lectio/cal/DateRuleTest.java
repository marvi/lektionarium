/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import lectio.cal.DateRule.BackedOffFrom;
import lectio.cal.DateRule.Earliest;
import lectio.cal.DateRule.Fixed;
import lectio.cal.DateRule.FromFirstAdvent;
import lectio.cal.DateRule.FromNextAdvent;
import lectio.cal.DateRule.WeekdayBetween;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Optional;

import static lectio.cal.DateRule.fixed;
import static lectio.cal.DateRule.fromEaster;
import static lectio.cal.DateRule.saturdayBetween;
import static lectio.cal.DateRule.sundayBetween;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marvi
 */
class DateRuleTest {

  private static final Anchors Y2027 = Anchors.of(2027);

  @Test
  void ankarnaForEttKyrkoar() {
    assertEquals(LocalDate.of(2027, 3, 28), Y2027.easter());
    assertEquals(LocalDate.of(2026, 11, 29), Y2027.firstAdvent());
    assertEquals(LocalDate.of(2027, 11, 28), Y2027.nextAdvent());
    // Advent när 27 november själv är en söndag, och när 3 december är det.
    assertEquals(LocalDate.of(2022, 11, 27), Anchors.adventOf(2022));
    assertEquals(LocalDate.of(2023, 12, 3), Anchors.adventOf(2023));
  }

  @Test
  void fastDatumKanLiggaAretInnan() {
    assertEquals(Optional.of(LocalDate.of(2027, 1, 6)), fixed(1, 6).resolve(Y2027));
    assertEquals(Optional.of(LocalDate.of(2026, 12, 24)), new Fixed(12, 24, -1).resolve(Y2027));
  }

  @Test
  void avstandFranPaskenOchAdvent() {
    assertEquals(Optional.of(LocalDate.of(2027, 2, 7)), fromEaster(-49).resolve(Y2027));
    assertEquals(Optional.of(LocalDate.of(2027, 5, 16)), fromEaster(49).resolve(Y2027));
    assertEquals(Optional.of(LocalDate.of(2026, 12, 20)), new FromFirstAdvent(21).resolve(Y2027));
    assertEquals(Optional.of(LocalDate.of(2027, 11, 21)), new FromNextAdvent(-7).resolve(Y2027));
  }

  @Test
  @DisplayName("veckodag i intervall: båda ändarna räknas, och tomt när ingen ryms")
  void veckodagIIntervall() {
    DateRule candlemas = sundayBetween(MonthDay.of(2, 2), MonthDay.of(2, 8));
    assertEquals(Optional.of(LocalDate.of(2014, 2, 2)), candlemas.resolve(Anchors.of(2014)));
    assertEquals(Optional.of(LocalDate.of(2026, 2, 8)), candlemas.resolve(Anchors.of(2026)));

    DateRule sundayAfterNewYear = sundayBetween(MonthDay.of(1, 2), MonthDay.of(1, 5));
    assertEquals(Optional.of(LocalDate.of(2015, 1, 4)), sundayAfterNewYear.resolve(Anchors.of(2015)));
    assertTrue(sundayAfterNewYear.resolve(Anchors.of(2023)).isEmpty(), "nyårsdagen 2023 är en söndag");

    DateRule midsummer = saturdayBetween(MonthDay.of(6, 20), MonthDay.of(6, 26));
    assertEquals(Optional.of(LocalDate.of(2020, 6, 20)), midsummer.resolve(Anchors.of(2020)));

    DateRule sundayAfterChristmas = new WeekdayBetween(DayOfWeek.SUNDAY, MonthDay.of(12, 27), MonthDay.of(12, 31), -1);
    assertEquals(Optional.of(LocalDate.of(2013, 12, 29)), sundayAfterChristmas.resolve(Anchors.of(2014)));
    assertTrue(sundayAfterChristmas.resolve(Anchors.of(2023)).isEmpty(), "juldagen 2022 är en söndag");
  }

  @Test
  @DisplayName("Kyndelsmässodagen backar en vecka när den krockar med Fastlagssöndagen")
  void backarVidKrock() {
    DateRule candlemas = new BackedOffFrom(sundayBetween(MonthDay.of(2, 2), MonthDay.of(2, 8)), fromEaster(-49));
    assertEquals(Optional.of(LocalDate.of(2008, 1, 27)), candlemas.resolve(Anchors.of(2008)));
    assertEquals(Optional.of(LocalDate.of(2014, 2, 2)), candlemas.resolve(Anchors.of(2014)));
  }

  @Test
  @DisplayName("Bebådelsedagen är söndagen 22-28 mars, dock senast söndagen före palmsöndagen")
  void tidigastAvTva() {
    DateRule annunciation = new Earliest(sundayBetween(MonthDay.of(3, 22), MonthDay.of(3, 28)), fromEaster(-14));
    assertEquals(Optional.of(LocalDate.of(2026, 3, 22)), annunciation.resolve(Anchors.of(2026)));
    assertEquals(Optional.of(LocalDate.of(2013, 3, 17)), annunciation.resolve(Anchors.of(2013)), "22-28 mars är palmsöndag");
    assertEquals(Optional.of(LocalDate.of(2027, 3, 14)), annunciation.resolve(Y2027), "22-28 mars är påskdagen");
    assertEquals(Optional.of(LocalDate.of(2008, 3, 9)), annunciation.resolve(Anchors.of(2008)), "tidigast möjliga");
  }
}

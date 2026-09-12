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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.IntFunction;

import static lectio.cal.ChurchYearRules.Rank.FEAST;
import static lectio.cal.ChurchYearRules.Rank.NIGHT;
import static lectio.cal.ChurchYearRules.Rank.NUMBERED_SUNDAY;
import static lectio.cal.ChurchYearRules.Rank.SUNDAY;
import static lectio.cal.DateRule.fixed;
import static lectio.cal.DateRule.fromEaster;
import static lectio.cal.DateRule.saturdayBetween;
import static lectio.cal.DateRule.sundayBetween;
import static lectio.cal.LiturgicalColor.GREEN;
import static lectio.cal.LiturgicalColor.RED;
import static lectio.cal.LiturgicalColor.VIOLET;
import static lectio.cal.LiturgicalColor.WHITE;

/**
 * Kyrkoårets dagar enligt Svenska kyrkans kyrkoordning: en rad per dag med
 * datumrecept, färg och rang, samt de två serierna av numrerade söndagar.
 * <p>
 * Hamnar två dagar på samma datum vinner den med högre rang. Lika rang på
 * samma datum är ett fel i tabellen och ger ett undantag vid utläggningen,
 * så ordningen i tabellen saknar betydelse.
 *
 * @author marvi
 */
final class ChurchYearRules {

  /** Vad som viker för vad när två dagar hamnar på samma datum. Högre vinner. */
  enum Rank {
    /** Söndagarna efter trettondedagen och efter trefaldighet, även de med egna namn. */
    NUMBERED_SUNDAY,
    /** Nattgudstjänster. Julnatten viker för fjärde advent, se issue #17. */
    NIGHT,
    /** Övriga söndagar. */
    SUNDAY,
    /** Helgdagar som tränger undan söndagar. */
    FEAST
  }

  record Feast(String name, LiturgicalColor color, Rank rank, DateRule rule) {}

  /** Numrerade söndagar från {@code first} fram till, men inte med, {@code until}. */
  record Series(DateRule first, DateRule until, IntFunction<String> name, IntFunction<LiturgicalColor> color) {}

  /** En dag på plats i ett kyrkoår, innan läsningarna hämtats. */
  record Placement(String name, LiturgicalColor color, Rank rank) {}

  private static final String[] ORDINALS = {
    "Första", "Andra", "Tredje", "Fjärde", "Femte",
    "Sjätte", "Sjunde", "Åttonde", "Nionde", "Tionde", "Elfte", "Tolfte", "Trettonde",
    "Fjortonde", "Femtonde", "Sextonde", "Sjuttonde", "Artonde", "Nittonde", "Tjugonde",
    "Tjugoförsta", "Tjugoandra", "Tjugotredje", "Tjugofjärde", "Tjugofemte", "Tjugosjätte",
    "Tjugosjunde"};

  static final List<Feast> FEASTS = List.of(
    // Advent, året innan
    new Feast("Första söndagen i advent", WHITE, SUNDAY, new FromFirstAdvent(0)),
    new Feast("Andra söndagen i advent", VIOLET, SUNDAY, new FromFirstAdvent(7)),
    new Feast("Tredje söndagen i advent", VIOLET, SUNDAY, new FromFirstAdvent(14)),
    new Feast("Fjärde söndagen i advent", VIOLET, SUNDAY, new FromFirstAdvent(21)),

    // Jul och nyår
    new Feast("Julnatten", WHITE, NIGHT, new Fixed(12, 24, -1)),
    new Feast("Juldagen", WHITE, FEAST, new Fixed(12, 25, -1)),
    new Feast("Annandag jul", WHITE, FEAST, new Fixed(12, 26, -1)),
    new Feast("Söndagen efter jul", WHITE, SUNDAY, new WeekdayBetween(DayOfWeek.SUNDAY, md(12, 27), md(12, 31), -1)),
    new Feast("Nyårsdagen", WHITE, FEAST, fixed(1, 1)),
    new Feast("Söndagen efter nyår", WHITE, SUNDAY, sundayBetween(md(1, 2), md(1, 5))),
    new Feast("Trettondedag jul", WHITE, FEAST, fixed(1, 6)),
    // Söndagen 2-8 februari, men en vecka tidigare om den krockar med Fastlagssöndagen.
    new Feast("Kyndelsmässodagen", WHITE, FEAST,
      new BackedOffFrom(sundayBetween(md(2, 2), md(2, 8)), fromEaster(-49))),

    // Fastan
    new Feast("Septuagesima", VIOLET, SUNDAY, fromEaster(-63)),
    new Feast("Sexagesima", VIOLET, SUNDAY, fromEaster(-56)),
    new Feast("Fastlagssöndagen", VIOLET, SUNDAY, fromEaster(-49)),
    new Feast("Askonsdagen", VIOLET, FEAST, fromEaster(-46)),
    new Feast("Första söndagen i fastan", VIOLET, SUNDAY, fromEaster(-42)),
    new Feast("Andra söndagen i fastan", VIOLET, SUNDAY, fromEaster(-35)),
    new Feast("Tredje söndagen i fastan", VIOLET, SUNDAY, fromEaster(-28)),
    new Feast("Midfastosöndagen", VIOLET, SUNDAY, fromEaster(-21)),
    new Feast("Femte söndagen i fastan", VIOLET, SUNDAY, fromEaster(-14)),
    // Söndagen 22-28 mars, dock senast söndagen före palmsöndagen.
    new Feast("Jungfru Marie bebådelsedag", WHITE, FEAST,
      new Earliest(sundayBetween(md(3, 22), md(3, 28)), fromEaster(-14))),

    // Stilla veckan och påsktiden
    new Feast("Palmsöndagen", WHITE, SUNDAY, fromEaster(-7)),
    new Feast("Skärtorsdagen", WHITE, FEAST, fromEaster(-3)),
    new Feast("Långfredagen", WHITE, FEAST, fromEaster(-2)),
    new Feast("Påsknatten", WHITE, NIGHT, fromEaster(-1)),
    new Feast("Påskdagen", WHITE, FEAST, fromEaster(0)),
    new Feast("Annandag påsk", WHITE, FEAST, fromEaster(1)),
    new Feast("Andra söndagen i påsktiden", WHITE, SUNDAY, fromEaster(7)),
    new Feast("Tredje söndagen i påsktiden", WHITE, SUNDAY, fromEaster(14)),
    new Feast("Fjärde söndagen i påsktiden", WHITE, SUNDAY, fromEaster(21)),
    new Feast("Femte söndagen i påsktiden", WHITE, SUNDAY, fromEaster(28)),
    new Feast("Bönsöndagen", WHITE, SUNDAY, fromEaster(35)),
    new Feast("Kristi himmelsfärds dag", WHITE, FEAST, fromEaster(39)),
    new Feast("Söndagen före pingst", WHITE, SUNDAY, fromEaster(42)),
    new Feast("Pingstdagen", RED, FEAST, fromEaster(49)),
    new Feast("Annandag pingst", RED, FEAST, fromEaster(50)),
    new Feast("Heliga trefaldighets dag", WHITE, FEAST, fromEaster(56)),

    // Sommar och höst
    new Feast("Midsommardagen", GREEN, FEAST, saturdayBetween(md(6, 20), md(6, 26))),
    new Feast("Den helige Johannes Döparens dag", RED, FEAST, sundayBetween(md(6, 21), md(6, 27))),
    new Feast("Den helige Mikaels dag", GREEN, FEAST, sundayBetween(md(9, 29), md(10, 5))),
    new Feast("Tacksägelsedagen", GREEN, FEAST, sundayBetween(md(10, 8), md(10, 14))),
    new Feast("Alla helgons dag", WHITE, FEAST, saturdayBetween(md(10, 31), md(11, 6))),
    new Feast("Söndagen efter alla helgons dag", WHITE, FEAST, sundayBetween(md(11, 1), md(11, 7))),

    // Kyrkoårets slut
    new Feast("Söndagen före domssöndagen", GREEN, SUNDAY, new FromNextAdvent(-14)),
    new Feast("Domssöndagen", GREEN, SUNDAY, new FromNextAdvent(-7)));

  static final List<Series> SERIES = List.of(
    // Söndagarna efter trettondedagen, fram till Septuagesima.
    new Series(sundayBetween(md(1, 7), md(1, 13)), fromEaster(-63),
      i -> ORDINALS[i] + " söndagen efter trettondedagen", i -> GREEN),
    // Söndagarna efter trefaldighet, fram till söndagen före domssöndagen.
    new Series(fromEaster(63), new FromNextAdvent(-14),
      i -> switch (i) {
        case 4 -> "Apostladagen";
        case 6 -> "Kristi förklarings dag";
        default -> ORDINALS[i] + " söndagen efter trefaldighet";
      },
      i -> switch (i) {
        case 4 -> RED;
        case 6 -> WHITE;
        default -> GREEN;
      }));

  private ChurchYearRules() {
  }

  private static MonthDay md(int month, int day) {
    return MonthDay.of(month, day);
  }

  /** Lägger ut kyrkoåret runt ankarna. */
  static SortedMap<LocalDate, Placement> layOut(Anchors anchors) {
    return layOut(anchors, FEASTS, SERIES);
  }

  static SortedMap<LocalDate, Placement> layOut(Anchors anchors, List<Feast> feasts, List<Series> series) {
    TreeMap<LocalDate, Placement> days = new TreeMap<>();
    for (Feast feast : feasts) {
      feast.rule().resolve(anchors)
        .ifPresent(date -> place(days, date, new Placement(feast.name(), feast.color(), feast.rank())));
    }
    for (Series s : series) {
      LocalDate until = s.until().resolve(anchors).orElseThrow();
      LocalDate sunday = s.first().resolve(anchors).orElseThrow();
      for (int i = 0; sunday.isBefore(until); i++, sunday = sunday.plusDays(7)) {
        place(days, sunday, new Placement(s.name().apply(i), s.color().apply(i), NUMBERED_SUNDAY));
      }
    }
    return days;
  }

  /** Den enda krockregeln: högre rang vinner, lika rang är ett fel i tabellen. */
  private static void place(TreeMap<LocalDate, Placement> days, LocalDate date, Placement placement) {
    Placement existing = days.get(date);
    if (existing == null || placement.rank().compareTo(existing.rank()) > 0) {
      days.put(date, placement);
    } else if (placement.rank() == existing.rank()) {
      throw new IllegalStateException(
        date + ": " + placement.name() + " och " + existing.name() + " har samma rang");
    }
  }
}

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
import java.util.Collections;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Ett kyrkoår i Svenska kyrkans tradition.
 * <p>
 * Kalenderår och kyrkoår sammanfaller inte. Kyrkoåret börjar första söndagen i
 * advent under föregående kalenderår. Året som används i den här klassen är året
 * för kyrkoårets påskdag: kyrkoåret 2013 börjar alltså 2012-12-02.
 * <p>
 * Instanser är oföränderliga och kan delas mellan trådar. Skapa dem via
 * {@link LiturgicalYearFactory}, som återanvänder redan uträknade år.
 * <p>
 * Inspiration från <a href="http://www.lysator.liu.se/alma/alma.cgi">alma</a>.
 * Se även <a href="https://www.svenskakyrkan.se/filer/KO_1_jan_2020.pdf">Svenska
 * kyrkans kyrkoordning</a>.
 *
 * @author marvi
 */
public class LiturgicalYear {

  /** Första året evangelieboken från 2003 års kyrkohandbok täcker fullt ut. */
  public static final int FIRST_SUPPORTED_YEAR = 2004;

  private static final String[] ORDINALS = {
    "Första", "Andra", "Tredje", "Fjärde", "Femte",
    "Sjätte", "Sjunde", "Åttonde", "Nionde", "Tionde", "Elfte", "Tolfte", "Trettonde",
    "Fjortonde", "Femtonde", "Sextonde", "Sjuttonde", "Artonde", "Nittonde", "Tjugonde",
    "Tjugoförsta", "Tjugoandra", "Tjugotredje", "Tjugofjärde", "Tjugofemte", "Tjugosjätte",
    "Tjugosjunde"};

  private final int year;
  private final LocalDate easterDay;
  private final int readingCycle;
  private final int easterSeries;
  private final ReadingCycles readingCycles;
  private final SortedMap<LocalDate, Day> daysOfYear = new TreeMap<>();

  /**
   * @param year kyrkoåret, måste vara {@value #FIRST_SUPPORTED_YEAR} eller senare
   * @throws IllegalArgumentException om året ligger före {@value #FIRST_SUPPORTED_YEAR}
   */
  public LiturgicalYear(int year) {
    if (year < FIRST_SUPPORTED_YEAR) {
      throw new IllegalArgumentException(
        "Endast år från och med " + FIRST_SUPPORTED_YEAR + " stöds, fick " + year);
    }
    this.year = year;
    this.readingCycle = getReadingCycle(year);
    this.easterSeries = getEasterSeries(year);
    this.easterDay = CalculateEaster.forYear(year);
    this.readingCycles = LectioRepository.getLectio();
    populateDaysOfYear();
  }

  /**
   * Placerar kyrkoårets dagar på faktiska datum.
   * <p>
   * Ordningen är betydelsefull: rörliga dagar som kan krocka med varandra läggs
   * ut med uttryckliga undantag, i samma ordning som kyrkoordningen anger dem.
   */
  private void populateDaysOfYear() {
    // Fasta dagar kring jul och nyår
    put("Julnatten", LocalDate.of(year - 1, 12, 24), LiturgicalColor.WHITE);
    put("Juldagen", LocalDate.of(year - 1, 12, 25), LiturgicalColor.WHITE);
    put("Annandag jul", LocalDate.of(year - 1, 12, 26), LiturgicalColor.WHITE);
    put("Nyårsdagen", LocalDate.of(year, 1, 1), LiturgicalColor.WHITE);
    put("Trettondedag jul", LocalDate.of(year, 1, 6), LiturgicalColor.WHITE);

    // Söndagen efter jul infaller inte varje år.
    LocalDate sundayAfterChristmas = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year - 1, 12, 26));
    if (sundayAfterChristmas.isBefore(LocalDate.of(year, 1, 1))) {
      put("Söndagen efter jul", sundayAfterChristmas, LiturgicalColor.WHITE);
    }

    // Söndagen efter nyår infaller inte varje år.
    LocalDate sundayAfterNewYear = sundayAfterNewYear();
    if (sundayAfterNewYear != null) {
      put("Söndagen efter nyår", sundayAfterNewYear, LiturgicalColor.WHITE);
    }

    LocalDate candlemass = candlemass();
    put("Kyndelsmässodagen", candlemass, LiturgicalColor.WHITE);

    // Söndagar efter trettondedagen, fram till Septuagesima.
    LocalDate septuagesima = easterDay.minusDays(63);
    LocalDate firstAfterEpiphany = firstAfterEpiphany();
    int add = 0;
    int i = 0;
    while (i < 8) {
      LocalDate d = firstAfterEpiphany.plusDays(add);
      if (!d.isEqual(candlemass) && d.isBefore(septuagesima)) {
        put(ORDINALS[i] + " söndagen efter trettondedagen", d, LiturgicalColor.GREEN);
      } else if (d.isEqual(septuagesima) || d.isAfter(septuagesima)) {
        i = 8;
      }
      add = add + 7;
      i++;
    }

    // Jungfru Marie bebådelsedag: söndag 22-28 mars, eller söndagen före
    // palmsöndagen och då tidigast 8 mars.
    LocalDate annunciation = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 3, 21));
    if (annunciation.isAfter(easterDay.minusDays(8))) {
      annunciation = easterDay.minusDays(14);
    }
    put("Jungfru Marie bebådelsedag", annunciation, LiturgicalColor.WHITE);

    // Fastan. Dagar som krockar med Kyndelsmässodagen eller bebådelsedagen utgår.
    if (!candlemass.isEqual(septuagesima)) {
      put("Septuagesima", septuagesima, LiturgicalColor.VIOLET);
    }
    if (!candlemass.isEqual(easterDay.minusDays(56))) {
      put("Sexagesima", easterDay.minusDays(56), LiturgicalColor.VIOLET);
    }
    put("Fastlagssöndagen", easterDay.minusDays(49), LiturgicalColor.VIOLET);
    put("Askonsdagen", easterDay.minusDays(46), LiturgicalColor.VIOLET);
    if (!annunciation.isEqual(easterDay.minusDays(42))) {
      put("Första söndagen i fastan", easterDay.minusDays(42), LiturgicalColor.VIOLET);
    }
    if (!annunciation.isEqual(easterDay.minusDays(35))) {
      put("Andra söndagen i fastan", easterDay.minusDays(35), LiturgicalColor.VIOLET);
    }
    if (!annunciation.isEqual(easterDay.minusDays(28))) {
      put("Tredje söndagen i fastan", easterDay.minusDays(28), LiturgicalColor.VIOLET);
    }
    if (!annunciation.isEqual(easterDay.minusDays(21))) {
      put("Midfastosöndagen", easterDay.minusDays(21), LiturgicalColor.VIOLET);
    }
    if (!annunciation.isEqual(easterDay.minusDays(14))) {
      put("Femte söndagen i fastan", easterDay.minusDays(14), LiturgicalColor.VIOLET);
    }

    // Stilla veckan, påsken och tiden fram till trefaldighet
    put("Palmsöndagen", easterDay.minusDays(7), LiturgicalColor.WHITE);
    put("Måndag i Stilla veckan", easterDay.minusDays(6), LiturgicalColor.WHITE);
    put("Tisdag i Stilla veckan", easterDay.minusDays(5), LiturgicalColor.WHITE);
    put("Onsdag i Stilla veckan", easterDay.minusDays(4), LiturgicalColor.WHITE);
    put("Skärtorsdagen", easterDay.minusDays(3), LiturgicalColor.WHITE);
    put("Långfredagen", easterDay.minusDays(2), LiturgicalColor.WHITE);
    put("Påsknatten", easterDay.minusDays(1), LiturgicalColor.WHITE);
    put("Påskdagen", easterDay, LiturgicalColor.WHITE);
    put("Annandag påsk", easterDay.plusDays(1), LiturgicalColor.WHITE);
    put("Andra söndagen i påsktiden", easterDay.plusDays(7), LiturgicalColor.WHITE);
    put("Tredje söndagen i påsktiden", easterDay.plusDays(14), LiturgicalColor.WHITE);
    put("Fjärde söndagen i påsktiden", easterDay.plusDays(21), LiturgicalColor.WHITE);
    put("Femte söndagen i påsktiden", easterDay.plusDays(28), LiturgicalColor.WHITE);
    put("Bönsöndagen", easterDay.plusDays(35), LiturgicalColor.WHITE);
    put("Kristi himmelsfärds dag", easterDay.plusDays(39), LiturgicalColor.WHITE);
    put("Söndagen före pingst", easterDay.plusDays(42), LiturgicalColor.WHITE);
    put("Pingstdagen", easterDay.plusDays(49), LiturgicalColor.WHITE);
    put("Annandag pingst", easterDay.plusDays(50), LiturgicalColor.WHITE);
    put("Heliga trefaldighets dag", easterDay.plusDays(56), LiturgicalColor.WHITE);

    LocalDate midsummer = nextWeekdayOfType(DayOfWeek.SATURDAY, LocalDate.of(year, 6, 19));
    put("Midsommardagen", midsummer, LiturgicalColor.GREEN);

    // Alla helgons dag behövs för att lägga ut trefaldighetstiden.
    LocalDate allSaints = nextWeekdayOfType(DayOfWeek.SATURDAY, LocalDate.of(year, 10, 30));
    put("Alla helgons dag", allSaints, LiturgicalColor.WHITE);
    put("Söndagen efter alla helgons dag", allSaints.plusDays(1), LiturgicalColor.WHITE);

    // Advent inleder kyrkoåret, under föregående kalenderår.
    LocalDate firstAdvent = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year - 1, 11, 26));
    put("Första söndagen i advent", firstAdvent, LiturgicalColor.WHITE);
    put("Andra söndagen i advent", firstAdvent.plusDays(7), LiturgicalColor.VIOLET);
    put("Tredje söndagen i advent", firstAdvent.plusDays(14), LiturgicalColor.VIOLET);
    put("Fjärde söndagen i advent", firstAdvent.plusDays(21), LiturgicalColor.VIOLET);

    // Nästa kyrkoårs advent avgör var det här kyrkoåret slutar.
    LocalDate nextAdvent = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 11, 26));
    put("Domssöndagen", nextAdvent.minusDays(7), LiturgicalColor.GREEN);
    put("Söndagen före domssöndagen", nextAdvent.minusDays(14), LiturgicalColor.GREEN);

    // Söndagarna efter Heliga trefaldighets dag, fram till domssöndagen.
    LocalDate firstAfterTrinity = easterDay.plusDays(63);
    add = 0;
    i = 0;
    while (i < 27) {
      LocalDate sunday = firstAfterTrinity.plusDays(add);
      add = add + 7;
      if (sunday.isEqual(allSaints.plusDays(1))) {
        i++;
        continue;
      }
      if (sunday.isAfter(nextAdvent.minusDays(15))) {
        break;
      }
      // Några söndagar i trefaldighetstiden har egna namn, resten numreras.
      switch (i) {
        case 4 -> put("Apostladagen", sunday, LiturgicalColor.RED);
        case 6 -> put("Kristi förklarings dag", sunday, LiturgicalColor.WHITE);
        default -> put(ORDINALS[i] + " söndagen efter trefaldighet", sunday, LiturgicalColor.GREEN);
      }
      i++;
    }

    put("Den helige Johannes Döparens dag", midsummer.plusDays(1), LiturgicalColor.RED);

    LocalDate michaelmas = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 9, 28));
    put("Den helige Mikaels dag", michaelmas, LiturgicalColor.GREEN);

    // Andra söndagen i oktober, räknat från sista september.
    LocalDate thanksgiving = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 9, 30));
    thanksgiving = nextWeekdayOfType(DayOfWeek.SUNDAY, thanksgiving);
    put("Tacksägelsedagen", thanksgiving, LiturgicalColor.GREEN);
  }

  private void put(String name, LocalDate date, LiturgicalColor color) {
    daysOfYear.put(date, makeDay(name, date, color));
  }

  private Day makeDay(String name, LocalDate date, LiturgicalColor color) {
    return readingsFor(name)
      .<Day>map(readings -> new HolyDay(name, date, color, readings))
      .orElseGet(() -> new OrdinaryDay(name, date, color));
  }

  /** Stilla veckan och påsken följer påskserien, övriga dagar läsningsserien. */
  private Optional<Readings> readingsFor(String name) {
    int cycle = HolyDay.usesEasterSeries(name) ? easterSeries : readingCycle;
    return readingCycles.readingsFor(name, cycle);
  }

  /**
   * Kyndelsmässodagen: söndag 2-8 februari, men flyttas en vecka bakåt om den
   * skulle infalla på Fastlagssöndagen.
   */
  private LocalDate candlemass() {
    LocalDate candlemass = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 2, 1));
    if (candlemass.equals(easterDay.minusDays(49))) {
      return candlemass.minusDays(7);
    }
    return candlemass;
  }

  /**
   * Finns det en söndag mellan nyårsdagen och Trettondedag jul blir den
   * "Söndagen efter nyår".
   *
   * @return datumet, eller null om året saknar en sådan söndag
   */
  private LocalDate sundayAfterNewYear() {
    LocalDate candidate = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 1, 1));
    return candidate.getDayOfMonth() < 6 ? candidate : null;
  }

  private LocalDate firstAfterEpiphany() {
    return nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 1, 6));
  }

  /**
   * Kyrkoårets dagar, ordnade efter datum.
   *
   * @return en oföränderlig vy med datum som nyckel
   */
  public SortedMap<LocalDate, Day> getDaysOfYear() {
    return Collections.unmodifiableSortedMap(daysOfYear);
  }

  /**
   * @param name dagens namn i kyrkoåret
   * @return dagen, om den finns detta kyrkoår och har egna texter
   */
  public Optional<HolyDay> findHolyDayByName(String name) {
    return daysOfYear.values().stream()
      .filter(day -> day.name().equals(name))
      .filter(HolyDay.class::isInstance)
      .map(HolyDay.class::cast)
      .findFirst();
  }

  /** @return läsningsserien, 1-3, för det här kyrkoåret */
  public int getReadingCycle() {
    return readingCycle;
  }

  /**
   * Läsningsserien löper i treårscykler från 2003.
   *
   * @param year ett kyrkoår
   * @return serien, 1-3, eller 0 för år före 1986
   */
  public static int getReadingCycle(int year) {
    if (year < 1986) {
      return 0;
    } else if (year < 2003) {
      return (year - 1986) % 3 + 1;
    }
    return (year - 2003) % 3 + 1;
  }

  /** @return påskserien, 1-4, för det här kyrkoåret */
  public int getEasterSeries() {
    return easterSeries;
  }

  /**
   * Påskserien löper i fyraårscykler från 2004.
   *
   * @param year ett kyrkoår
   * @return serien, 1-4, eller 0 för år före 2004
   */
  public static int getEasterSeries(int year) {
    if (year < FIRST_SUPPORTED_YEAR) {
      return 0;
    }
    return (year - FIRST_SUPPORTED_YEAR) % 4 + 1;
  }

  /** @return kyrkoåret, räknat efter sin påskdag */
  public int getYear() {
    return year;
  }

  /** @return påskdagen detta kyrkoår */
  public LocalDate getEasterDay() {
    return easterDay;
  }

  /**
   * @param dayOfWeek veckodagen att leta efter
   * @param from      datumet att räkna från, exklusive
   * @return nästa datum efter {@code from} som infaller på veckodagen
   */
  protected static LocalDate nextWeekdayOfType(DayOfWeek dayOfWeek, LocalDate from) {
    return from.with(TemporalAdjusters.next(dayOfWeek));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("LiturgicalYear{year=").append(year).append('\n');
    daysOfYear.values().forEach(day -> sb.append("  ").append(day).append('\n'));
    return sb.append('}').toString();
  }
}

/*
 * Copyright (c) 2010, 2020, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.logging.Logger;


/**
 * Represents a Liturgical year in the Church of Sweden tradition.<br>
 * <p>
 * Note that calendar year and liturgical year don't align. The liturgical
 * year starts first Sunday of Advent the previous calendar year. <br>
 * The year used in this class is the year of the Easter Sunday of this liturgical year.
 * I.e. the liturgical year 2013 starts 2012-12-02.<br>
 * <p>
 * Inspiration from http://www.lysator.liu.se/alma/alma.cgi
 * <p>
 * https://www.svenskakyrkan.se/filer/KO_1_jan_2020.pdf
 *
 * @author marvi
 */
public class LiturgicalYear {

  private static final Logger LOGGER = Logger.getLogger( LiturgicalYear.class.getName() );

  private final int year;

  private final String[] swedishCounting = new String[]{"Första", "Andra", "Tredje", "Fjärde", "Femte",
    "Sjätte", "Sjunde", "Åttonde", "Nionde", "Tionde", "Elfte", "Tolfte", "Trettonde",
    "Fjortonde", "Femtonde", "Sextonde", "Sjuttonde", "Artonde", "Nittonde", "Tjugonde",
    "Tjugoförsta", "Tjugoandra", "Tjugotredje", "Tjugofjärde", "Tjugofemte", "Tjugosjätte",
    "Tjugosjunde"};

  private final LocalDate easterDay;
  private int readingCycle;
  private int easterSeries;
  private SortedMap<LocalDate, LiturgicalDay> daysOfYear = new TreeMap<>();
  private ReadingCycles readingCycles;

  /**
   * @param year The year to use. Has to be > 2004.
   */
  public LiturgicalYear(int year) {
    LOGGER.info("Creating Liturgical Year for " + year);
    this.year = year;
    if(year < 2004) {
      throw new UnsupportedOperationException("Only years after 2004 is supported for this library");
    }
    this.readingCycle = getReadingCycle();
    this.easterSeries = getEasterSeries(year);
    this.easterDay = CalculateEaster.forYear(year);
    this.readingCycles = LectioRepository.getLectio();
    populateDaysOfYear();
  }

  /**
   * Maps the holidays to actual dates for this year.
   */
  private void populateDaysOfYear() {
    long startTime = System.nanoTime();

    // Fixed days
    daysOfYear.put(LocalDate.of(year - 1, 12, 24),
      makeDay("Julnatten", LocalDate.of(year - 1, 12, 24), LiturgicalColor.WHITE));
    daysOfYear.put(LocalDate.of(year - 1, 12, 25),
      makeDay("Juldagen", LocalDate.of(year - 1, 12, 25), LiturgicalColor.WHITE));
    daysOfYear.put(LocalDate.of(year - 1, 12, 26),
      makeDay("Annandag jul", LocalDate.of(year - 1, 12, 26), LiturgicalColor.WHITE));
    daysOfYear.put(LocalDate.of(year, 1, 1),
      makeDay("Nyårsdagen", LocalDate.of(year, 1, 1), LiturgicalColor.WHITE));
    daysOfYear.put(LocalDate.of(year, 1, 6),
      makeDay("Trettondedag jul", LocalDate.of(year, 1, 6), LiturgicalColor.WHITE));

    // Söndagen efter jul infaller inte varje år.
    LocalDate sej = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of((year - 1), 12, 26));
    if (sej.isBefore(LocalDate.of(year, 1, 1))) {
      daysOfYear.put(sej, makeDay("Söndagen efter jul", sej, LiturgicalColor.WHITE));
    }

    // "Söndagen efter nyår" infaller inte varje år.
    if (sundayAfterNewYear() != null) {
      daysOfYear.put(sundayAfterNewYear(), makeDay("Söndagen efter nyår", sundayAfterNewYear(), LiturgicalColor.WHITE));
    }

    // Kyndelsmäss
    LocalDate kynd = candlemass();
    daysOfYear.put(kynd, makeDay("Kyndelsmässodagen", kynd, LiturgicalColor.WHITE));

    // Söndagar efter trettondedagen
    int add = 0;
    int i = 0;
    while (i < 8) {
      LocalDate d = firstAfterEpifania().plusDays(add);
      if (!d.isEqual(kynd) && d.isBefore(easterDay.minusDays(63))) {
        String prefix = swedishCounting[i];
        daysOfYear.put(d, makeDay(prefix + " söndagen efter trettondedagen", d, LiturgicalColor.GREEN));
      } else if (d.isEqual(easterDay.minusDays(63))
        || d.isAfter(easterDay.minusDays(63))) {
        i = 8;
      }
      add = add + 7;
      i++;
    }

    // Jungfru Marie bebådelsedag. Söndag 22-28 mars eller söndag före Palmsöndag och då tidigast 8 mars
    LocalDate jmb = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 3, 21));
    if (jmb.isAfter(easterDay.minusDays(8))) {
      jmb = easterDay.minusDays(14);
    }
    daysOfYear.put(jmb, makeDay("Jungfru Marie bebådelsedag", jmb, LiturgicalColor.WHITE));
    if (!kynd.isEqual(easterDay.minusDays(63))) {
      daysOfYear.put(easterDay.minusDays(63), makeDay("Septuagesima", easterDay.minusDays(63), LiturgicalColor.VIOLET));
    }
    if (!kynd.isEqual(easterDay.minusDays(56))) {
      daysOfYear.put(easterDay.minusDays(56), makeDay("Sexagesima", easterDay.minusDays(56), LiturgicalColor.VIOLET));
    }
    daysOfYear.put(easterDay.minusDays(49), makeDay("Fastlagssöndagen", easterDay.minusDays(49), LiturgicalColor.VIOLET));
    daysOfYear.put(easterDay.minusDays(46), makeDay("Askonsdagen", easterDay.minusDays(46), LiturgicalColor.VIOLET));
    if (!jmb.isEqual(easterDay.minusDays(42))) {
      daysOfYear.put(easterDay.minusDays(42), makeDay("Första söndagen i fastan", easterDay.minusDays(42), LiturgicalColor.VIOLET));
    }
    if (!jmb.isEqual(easterDay.minusDays(35))) {
      daysOfYear.put(easterDay.minusDays(35), makeDay("Andra söndagen i fastan", easterDay.minusDays(35), LiturgicalColor.VIOLET));
    }
    if (!jmb.isEqual(easterDay.minusDays(28))) {
      daysOfYear.put(easterDay.minusDays(28), makeDay("Tredje söndagen i fastan", easterDay.minusDays(28), LiturgicalColor.VIOLET));
    }
    if (!jmb.isEqual(easterDay.minusDays(21))) {
      daysOfYear.put(easterDay.minusDays(21), makeDay("Midfastosöndagen", easterDay.minusDays(21), LiturgicalColor.VIOLET));
    }
    if (!jmb.isEqual(easterDay.minusDays(14))) {
      daysOfYear.put(easterDay.minusDays(14), makeDay("Femte söndagen i fastan", easterDay.minusDays(14), LiturgicalColor.VIOLET));
    }

    // Utgår från påsken
    daysOfYear.put(easterDay, makeDay("Påskdagen", easterDay, LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.minusDays(1), makeDay("Påsknatten", easterDay.minusDays(1), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.minusDays(2), makeDay("Långfredagen", easterDay.minusDays(2), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.minusDays(3), makeDay("Skärtorsdagen", easterDay.minusDays(3), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.minusDays(4), makeDay("Onsdag i Stilla veckan", easterDay.minusDays(4), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.minusDays(5), makeDay("Tisdag i Stilla veckan", easterDay.minusDays(5), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.minusDays(6), makeDay("Måndag i Stilla veckan", easterDay.minusDays(6), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.minusDays(7), makeDay("Palmsöndagen", easterDay.minusDays(7), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(1), makeDay("Annandag påsk", easterDay.plusDays(1), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(7), makeDay("Andra söndagen i påsktiden", easterDay.plusDays(7), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(14), makeDay("Tredje söndagen i påsktiden", easterDay.plusDays(14), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(21), makeDay("Fjärde söndagen i påsktiden", easterDay.plusDays(21), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(28), makeDay("Femte söndagen i påsktiden", easterDay.plusDays(28), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(35), makeDay("Bönsöndagen", easterDay.plusDays(35), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(39), makeDay("Kristi himmelsfärds dag", easterDay.plusDays(39), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(42), makeDay("Söndagen före pingst", easterDay.plusDays(42), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(49), makeDay("Pingstdagen", easterDay.plusDays(49), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(50), makeDay("Annandag pingst", easterDay.plusDays(50), LiturgicalColor.WHITE));
    daysOfYear.put(easterDay.plusDays(56), makeDay("Heliga trefaldighets dag", easterDay.plusDays(56), LiturgicalColor.WHITE));


    // Midsommardagen
    LocalDate msd = nextWeekdayOfType(DayOfWeek.SATURDAY, LocalDate.of(year, 6, 19));
    daysOfYear.put(msd, makeDay("Midsommardagen", msd, LiturgicalColor.GREEN));

    // Alla helons dag (behövs för att lägga ut trefaldighetstiden)
    LocalDate ahd = nextWeekdayOfType(DayOfWeek.SATURDAY, LocalDate.of(year, 10, 30));
    daysOfYear.put(ahd, makeDay("Alla helgons dag", ahd, LiturgicalColor.WHITE));
    daysOfYear.put(ahd.plusDays(1), makeDay("Söndagen efter alla helgons dag", ahd.plusDays(1), LiturgicalColor.WHITE));

    // Advent och kyrkoårets slut
    LocalDate firstAdv = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of((year - 1), 11, 26));
    daysOfYear.put(firstAdv, makeDay("Första söndagen i advent", firstAdv, LiturgicalColor.WHITE));
    daysOfYear.put(firstAdv.plusDays(7), makeDay("Andra söndagen i advent", firstAdv.plusDays(7), LiturgicalColor.VIOLET));
    daysOfYear.put(firstAdv.plusDays(14), makeDay("Tredje söndagen i advent", firstAdv.plusDays(14), LiturgicalColor.VIOLET));
    daysOfYear.put(firstAdv.plusDays(21), makeDay("Fjärde söndagen i advent", firstAdv.plusDays(21), LiturgicalColor.VIOLET));

    // Kalenderårsadvent (för att räkna ut Domssöndag)
    LocalDate calFirstAdv = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of((year), 11, 26));
    daysOfYear.put(calFirstAdv.minusDays(7), makeDay("Domssöndagen", calFirstAdv.minusDays(7), LiturgicalColor.GREEN));
    daysOfYear.put(calFirstAdv.minusDays(14), makeDay("Söndagen före domssöndagen", calFirstAdv.minusDays(14), LiturgicalColor.GREEN));

    // Trefaldighetssöndagarna
    // Söndagar efter Heliga Trefaldighet
    LocalDate firstAfterTre = easterDay.plusDays(63);
    add = 0;
    i = 0;
    while (i < 27) {
      LocalDate setre = firstAfterTre.plusDays(add);
      add = add + 7;
      if (setre.isEqual(ahd.plusDays(1))) {
        i++;
        continue;
      }
      if (setre.isAfter(calFirstAdv.minusDays(15))) {
        break;
      }
      String etreName = swedishCounting[i];
      // Hantera de speciella dagarna under trefaldighetstiden. Resten heter bara "nn söndagen efter trefaldighet".
      switch (i) {
        case 4:
          daysOfYear.put(setre, makeDay("Apostladagen", setre, LiturgicalColor.RED));
          break;
        case 6:
          daysOfYear.put(setre, makeDay("Kristi förklarings dag", setre, LiturgicalColor.WHITE));
          break;
        default:
          daysOfYear.put(setre, makeDay(etreName + " söndagen efter trefaldighet", setre, LiturgicalColor.GREEN));
          break;
      }
      i++;
    }

    // Johannes döparens dag
    daysOfYear.put(msd.plusDays(1), makeDay("Den helige Johannes Döparens dag", msd.plusDays(1), LiturgicalColor.RED));

    // Den helige Mikaels dag
    LocalDate hmd = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 9, 28));
    daysOfYear.put(hmd, makeDay("Den helige Mikaels dag", hmd, LiturgicalColor.GREEN));

    // Tacksägelsedagen
    LocalDate tsd = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 9, 30));
    tsd = nextWeekdayOfType(DayOfWeek.SUNDAY, tsd);
    daysOfYear.put(tsd, makeDay("Tacksägelsedagen", tsd, LiturgicalColor.GREEN));
    long endTime = System.nanoTime();
    LOGGER.info("populateDays took " + ((endTime - startTime) / 1000) + " ms");
  }

  private LiturgicalDay makeDay(String name, LocalDate date, LiturgicalColor color) {
    List<Memorial> mem = new ArrayList<>(); // Assuming Memorial is a class/record.
    Day dayObject = new Day(name, date, mem, color); // Create Day record instance first
    if(readingCycles.hasReadings(name)) {
      // HolyDay constructor now takes a Day object and Readings
      return new HolyDay(dayObject, getReadingsForDay(name, this.year));
    }
    else {
      return dayObject; // Return the created Day object
    }
  }

  /**
   * Kyndelsmässodagen
   * Söndag 2-8 februari eller söndag före Fastlagssöndag
   * (flyttas tillbaka en vecka om den infaller på Fastlagssöndagen)
   */
  @NotNull
  private LocalDate candlemass() {
    LocalDate kynd = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 2, 1));
    if (kynd.equals(easterDay.minusDays(49))) {
      // Skulle infallit på fastlagssöndagen. Flyttar.
      kynd = kynd.minusDays(7);
    }
    return kynd;
  }

  /**
   * Om det finns en söndag mellan nyårsdagen och Trettondedag jul blir den
   * "Söndag efter nyår".
   * @return LocalDate om det finns en söndag efter nyår detta år.
   */
  @Nullable
  private LocalDate sundayAfterNewYear() {
    LocalDate candidate = nextWeekdayOfType(DayOfWeek.SUNDAY, LocalDate.of(year, 1, 1));
    if (candidate.getDayOfMonth() < 6) {
      return candidate;
    }
    return null;
  }

  @NotNull
  private LocalDate firstAfterEpifania() {
    LocalDate ep = LocalDate.of(year, 1, 6);
    return nextWeekdayOfType(DayOfWeek.SUNDAY, ep);
  }

  @NotNull
  private Readings getReadingsForDay(String holyDay, int year) {
    int cycle = useEasterReadings(holyDay) ? getEasterSeries(year) : getReadingCycle(year);
    return readingCycles.readingsForCycle(holyDay, cycle);
  }

  public boolean useEasterReadings(String name) {
    return (name.equals("Palmsöndagen")
      || name.equals("Skärtorsdagen")
      || name.equals("Långfredagen")
      || name.equals("Påsknatten")
      || name.equals("Påskdagen"));
  }

  /**
   * @return a map with a date as key and the Day as value.
   */
  public SortedMap<LocalDate, LiturgicalDay> getDaysOfYear() {
    return daysOfYear;
  }

  public HolyDay getHolyDayByName(String name) {
    Optional<LiturgicalDay> match = daysOfYear.values().parallelStream()
      .filter(d -> d.name().equals(name) && d instanceof HolyDay)
      .findAny();
    if(match.isPresent()) {
      return (HolyDay) match.get();
    }
    else {
      LOGGER.severe("Could not find "  +  name + " in " + this.year);
      return null;
    }
  }

  public int getReadingCycle() {
    return getReadingCycle(this.year);
  }

  public static int getReadingCycle(int year) {
    if (year < 1986) {
      return 0;
    } else if (year < 2003) {
      return (year - 1986) % 3 + 1;
    }
    return (year - 2003) % 3 + 1;
  }

  public int getEasterSeries() {
    return getEasterSeries(this.year);
  }

  public static int getEasterSeries(int year) {
    if (year < 2004) {
      return 0;
    }
    return (year - 2004) % 4 + 1;
  }

  public int getYear() {
    return this.year;
  }

  /**
   * @param dow The day of week
   * @param d   The date to start from
   * @return The next date with this weekday
   */
  @NotNull
  protected static LocalDate nextWeekdayOfType(DayOfWeek dow, LocalDate d) {
    return d.with(TemporalAdjusters.next(dow));
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("LiturgicalYear{ year=");
    sb.append(year);
    sb.append(" ");
    Set<LocalDate> days = daysOfYear.keySet();
    for (LocalDate ld : days) {
      sb.append(daysOfYear.get(ld).toString());
      sb.append(" \n ");
    }
    sb.append(" }");
    return sb.toString();
  }

}

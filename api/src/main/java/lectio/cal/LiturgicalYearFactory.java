/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Ingången till biblioteket: skapar och återanvänder {@link LiturgicalYear}.
 * <p>
 * Uträkningen av ett kyrkoår är inte gratis, och resultatet ändras aldrig. Den
 * här klassen sparar därför varje uträknat år och har dessutom metoder för att
 * gå fram och tillbaka i kalendern.
 * <p>
 * Instanser är trådsäkra. Skapa gärna en och återanvänd den.
 *
 * <pre>{@code
 * LiturgicalYearFactory kalender = new LiturgicalYearFactory();
 * Day idag = kalender.getCurrentDay(LocalDate.now());
 * idag.findReadings().ifPresent(r -> System.out.println(r.go().sweRef()));
 * }</pre>
 *
 * @author marvi
 */
public class LiturgicalYearFactory {

  /**
   * Ingen dag i kyrkoåret ligger mer än ett år från närmaste granne, så en
   * sökning som gått längre än så letar efter något som inte finns.
   */
  private static final int MAX_SEARCH_DAYS = 400;

  /**
   * Hur många kyrkoår som sparas.
   * <p>
   * Cachen måste ha ett tak. Ett uträknat kyrkoår tar omkring 8 kB, och en
   * webbtjänst som låter anroparen välja årtal skulle annars kunna fås att
   * fylla minnet genom att be om år efter år. Taket räcker med god marginal
   * för att bläddra omkring i kalendern och för ett rullande flöde.
   */
  private static final int MAX_CACHED_YEARS = 64;

  private final Map<Integer, LiturgicalYear> cache =
    Collections.synchronizedMap(new LruCache(MAX_CACHED_YEARS));

  /**
   * @param year kyrkoåret, räknat efter sin påskdag
   * @return kyrkoåret, uträknat vid första anropet och därefter återanvänt
   */
  public LiturgicalYear getYear(int year) {
    LiturgicalYear cached = cache.get(year);
    if (cached != null) {
      return cached;
    }
    // Uträkningen sker utanför låset. Två trådar kan råka räkna ut samma år
    // samtidigt, vilket är ofarligt eftersom resultatet är detsamma.
    LiturgicalYear computed = new LiturgicalYear(year);
    cache.put(year, computed);
    return computed;
  }

  /** Minst använda året åker ut när taket nås. */
  private static final class LruCache extends LinkedHashMap<Integer, LiturgicalYear> {

    private final int maxEntries;

    LruCache(int maxEntries) {
      super(16, 0.75f, true);
      this.maxEntries = maxEntries;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<Integer, LiturgicalYear> eldest) {
      return size() > maxEntries;
    }
  }

  /**
   * Hittar den dag i kyrkoåret vi befinner oss i.
   * <p>
   * I kyrkoåret "bor" man kvar i den senaste infallna dagen tills nästa
   * infaller, så en tisdag efter påsk ger annandag påsk.
   *
   * @param date dagens datum
   * @return den dag i kyrkoåret som gäller
   */
  public Day getCurrentDay(LocalDate date) {
    SortedMap<LocalDate, Day> days = daysOf(date.getYear(), date.getYear() + 1);
    return searchBackwards(days, date);
  }

  /**
   * @param date datumet att utgå från
   * @return nästa dag i kyrkoåret efter {@code date}
   */
  public Day getNextDay(LocalDate date) {
    SortedMap<LocalDate, Day> days = daysOf(date.getYear(), date.getYear() + 1);
    LocalDate from = date.plusDays(1);
    SortedMap<LocalDate, Day> rest = days.tailMap(from);
    if (rest.isEmpty()) {
      throw new NoSuchElementException("Hittar ingen dag i kyrkoåret efter " + date);
    }
    return rest.get(rest.firstKey());
  }

  /**
   * Går först bakåt till den dag i kyrkoåret vi befinner oss i, och därifrån
   * vidare bakåt till dagen före den.
   *
   * @param date datumet att utgå från
   * @return föregående dag i kyrkoåret
   */
  public Day getPreviousDay(LocalDate date) {
    SortedMap<LocalDate, Day> days = daysOf(date.getYear() - 1, date.getYear(), date.getYear() + 1);
    LocalDate current = searchBackwards(days, date).date();
    SortedMap<LocalDate, Day> before = days.headMap(current);
    if (before.isEmpty()) {
      throw new NoSuchElementException("Hittar ingen dag i kyrkoåret före " + date);
    }
    return before.get(before.lastKey());
  }

  /**
   * @param year kyrkoåret
   * @return kyrkoåret, uträknat vid första anropet och därefter återanvänt
   */
  public LiturgicalYear getDaysOfLiturgicalYear(int year) {
    return getYear(year);
  }

  /**
   * Dagarna för ett spann av kyrkoår, sammanslagna.
   * <p>
   * Tänkt för ett flöde som ska prenumereras på: ett fönster kring dagens
   * datum i stället för ett år i taget.
   *
   * @param firstYear första kyrkoåret, inklusive
   * @param lastYear  sista kyrkoåret, inklusive
   * @return dagarna, ordnade efter datum
   * @throws IllegalArgumentException om spannet är bakvänt
   */
  public SortedMap<LocalDate, Day> getDaysOfLiturgicalYears(int firstYear, int lastYear) {
    if (lastYear < firstYear) {
      throw new IllegalArgumentException(
        "Spannet " + firstYear + "-" + lastYear + " är bakvänt");
    }
    TreeMap<LocalDate, Day> merged = new TreeMap<>();
    for (int year = firstYear; year <= lastYear; year++) {
      merged.putAll(getYear(year).getDaysOfYear());
    }
    return Collections.unmodifiableSortedMap(merged);
  }

  /**
   * När ett fönster av kyrkoår senast bytte innehåll.
   * <p>
   * Ett rullande fönster ändras först när ett nytt kyrkoår börjar, alltså vid
   * första söndagen i advent. Däremellan är innehållet oförändrat, vilket gör
   * det möjligt att svara med ETag och 304 i stället för att skicka om allt.
   *
   * @param year kyrkoåret som fönstret är centrerat kring
   * @return dagen det kyrkoåret började
   */
  public LocalDate startOfLiturgicalYear(int year) {
    return getYear(year).getDaysOfYear().firstKey();
  }

  /**
   * Kyrkoår och kalenderår sammanfaller inte. Den här metoden ger kyrkoårets
   * dagar som infaller under ett visst kalenderår.
   *
   * @param year kalenderåret
   * @return dagarna under kalenderåret, ordnade efter datum
   */
  public SortedMap<LocalDate, Day> getDaysOfCalendarYear(int year) {
    // Kyrkoåret som slutar detta kalenderår plus det som börjar i advent.
    SortedMap<LocalDate, Day> days = daysOf(year, year + 1);
    return Collections.unmodifiableSortedMap(
      days.subMap(LocalDate.of(year, 1, 1), LocalDate.of(year + 1, 1, 1)));
  }

  /**
   * @param year  kalenderåret
   * @param month månaden, 1-12
   * @return dagarna under den kalendermånaden, ordnade efter datum
   */
  public SortedMap<LocalDate, Day> getCalendarMonth(int year, int month) {
    LocalDate first = LocalDate.of(year, month, 1);
    SortedMap<LocalDate, Day> days = daysOf(year, year + 1);
    return Collections.unmodifiableSortedMap(days.subMap(first, first.plusMonths(1)));
  }

  /**
   * Vilket kyrkoår ett datum tillhör.
   * <p>
   * Från och med första söndagen i advent hör datumet till nästa kalenderårs
   * kyrkoår, dessförinnan till det innevarande.
   *
   * @param date ett datum
   * @return kyrkoåret som datumet tillhör
   */
  public LiturgicalYear getLiturgicalYear(LocalDate date) {
    LocalDate firstAdvent = findFirstAdvent(date.getYear())
      .orElseThrow(() -> new NoSuchElementException(
        "Hittar ingen första söndag i advent under " + date.getYear()));
    return date.isBefore(firstAdvent) ? getYear(date.getYear()) : getYear(date.getYear() + 1);
  }

  private Optional<LocalDate> findFirstAdvent(int calendarYear) {
    return getDaysOfCalendarYear(calendarYear).values().stream()
      .filter(day -> day.name().equals("Första söndagen i advent"))
      .map(Day::date)
      .findFirst();
  }

  /**
   * Slår ihop flera kyrkoår till en egen karta.
   * <p>
   * Kopian är viktig: {@link LiturgicalYear#getDaysOfYear()} ger en vy av årets
   * egna dagar, och att skriva i den skulle förorena cachen för alla anropare.
   */
  private SortedMap<LocalDate, Day> daysOf(int... years) {
    TreeMap<LocalDate, Day> merged = new TreeMap<>();
    for (int year : years) {
      merged.putAll(getYear(year).getDaysOfYear());
    }
    return merged;
  }

  private static Day searchBackwards(SortedMap<LocalDate, Day> days, LocalDate from) {
    SortedMap<LocalDate, Day> upToAndIncluding = days.headMap(from.plusDays(1));
    if (upToAndIncluding.isEmpty()
      || upToAndIncluding.lastKey().isBefore(from.minusDays(MAX_SEARCH_DAYS))) {
      throw new NoSuchElementException("Hittar ingen dag i kyrkoåret vid " + from);
    }
    return upToAndIncluding.get(upToAndIncluding.lastKey());
  }
}

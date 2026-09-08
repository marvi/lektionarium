/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.format;

import lectio.cal.Day;
import lectio.cal.LiturgicalYearFactory;
import lectio.cal.Reading;
import lectio.cal.Readings;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.SortedMap;

/**
 * Kyrkoåret som iCalendar enligt RFC 5545.
 * <p>
 * Dagarna skrivs som heldagshändelser, vilket är vad de är. UID:t härleds ur
 * datum och namn och är därmed stabilt: hämtar man om kalendern uppdaterar
 * klienten befintliga poster i stället för att lägga till dubbletter.
 *
 * @author marvi
 */
public final class IcalFormat {

  private static final String CRLF = "\r\n";
  private static final int MAX_OCTETS_PER_LINE = 75;
  private static final String PRODID = "-//marvi.io//lektionarium//SV";
  private static final String UID_DOMAIN = "@lektionarium.se";
  private static final String FEED_UID = "kyrkoaret" + UID_DOMAIN;
  private static final String DEFAULT_REFRESH_INTERVAL = "P1W";

  private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");
  private static final DateTimeFormatter TIMESTAMP =
    DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'").withZone(ZoneOffset.UTC);

  private IcalFormat() {
  }

  /**
   * @param year kyrkoåret
   * @return kalendern som iCalendar
   */
  public static String forLiturgicalYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.LITURGICAL, year);
  }

  /**
   * @param year kalenderåret
   * @return kalendern som iCalendar
   */
  public static String forCalendarYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.CALENDAR, year);
  }

  /**
   * @param factory kalendern att hämta dagarna ur
   * @param basis   om årtalet syftar på kyrkoår eller kalenderår
   * @param year    årtalet
   * @return kalendern som iCalendar
   */
  public static String forYear(LiturgicalYearFactory factory, CalendarBasis basis, int year) {
    SortedMap<LocalDate, Day> days = basis.days(factory, year);
    // DTSTAMP måste vara härledd ur innehållet, inte ur stunden. Ett förflutet
    // år ändras aldrig, och då ska två hämtningar ge samma bytes så att ETag
    // och 304 betyder något.
    Instant stamp = days.isEmpty()
      ? Instant.EPOCH
      : days.firstKey().atStartOfDay(ZoneOffset.UTC).toInstant();
    return forDays(days.values(), basis.calendarName(year), stamp);
  }

  /**
   * Ett flöde att prenumerera på, med uppgifterna ur RFC 7986 som talar om för
   * klienten var den hämtar om och hur ofta.
   * <p>
   * Anropas typiskt med ett rullande fönster av kyrkoår. Eftersom UID:t härleds
   * ur datum och namn känner klienten igen dagarna mellan hämtningarna och
   * uppdaterar dem i stället för att lägga till dubbletter.
   * <p>
   * DTSTAMP sätts till {@code lastModified} och inte till stundens tidpunkt.
   * Det gör utdatat identiskt mellan anrop så länge innehållet är oförändrat,
   * vilket i sin tur gör det meningsfullt att svara med ETag och 304.
   *
   * @param days         dagarna att skriva ut
   * @param calendarName namn på kalendern, visas i de flesta klienter
   * @param source       adressen klienten ska hämta om flödet ifrån
   * @param lastModified när innehållet senast ändrades
   * @return flödet som iCalendar
   */
  public static String forSubscription(Collection<Day> days, String calendarName,
                                       String source, Instant lastModified) {
    String stamp = TIMESTAMP.format(lastModified.truncatedTo(ChronoUnit.SECONDS));
    StringBuilder out = new StringBuilder();
    appendCalendarHeader(out, calendarName);
    line(out, "UID:" + FEED_UID);
    line(out, "LAST-MODIFIED:" + stamp);
    // SOURCE och URL är URI-värden och escapas därför inte som TEXT.
    line(out, "SOURCE;VALUE=URI:" + source);
    line(out, "URL;VALUE=URI:" + source);
    line(out, "REFRESH-INTERVAL;VALUE=DURATION:" + DEFAULT_REFRESH_INTERVAL);
    line(out, "X-PUBLISHED-TTL:" + DEFAULT_REFRESH_INTERVAL);
    for (Day day : days) {
      appendEvent(out, day, stamp);
    }
    line(out, "END:VCALENDAR");
    return out.toString();
  }

  /**
   * @param days         dagarna att skriva ut
   * @param calendarName namn på kalendern, visas i de flesta klienter
   * @param stamp        tidpunkt för DTSTAMP
   * @return dagarna som iCalendar
   */
  public static String forDays(Collection<Day> days, String calendarName, Instant stamp) {
    String dtstamp = TIMESTAMP.format(stamp.truncatedTo(ChronoUnit.SECONDS));
    StringBuilder out = new StringBuilder();
    appendCalendarHeader(out, calendarName);
    for (Day day : days) {
      appendEvent(out, day, dtstamp);
    }
    line(out, "END:VCALENDAR");
    return out.toString();
  }

  private static void appendCalendarHeader(StringBuilder out, String calendarName) {
    line(out, "BEGIN:VCALENDAR");
    line(out, "VERSION:2.0");
    line(out, "PRODID:" + PRODID);
    line(out, "CALSCALE:GREGORIAN");
    line(out, "METHOD:PUBLISH");
    line(out, "NAME:" + escape(calendarName));
    // X-WR-CALNAME är inte standard men är det äldre klienter faktiskt läser.
    line(out, "X-WR-CALNAME:" + escape(calendarName));
  }

  private static void appendEvent(StringBuilder out, Day day, String dtstamp) {
    line(out, "BEGIN:VEVENT");
    line(out, "UID:" + uid(day));
    line(out, "DTSTAMP:" + dtstamp);
    // Heldagshändelse: DTEND är exklusivt och pekar därför på nästa dag.
    line(out, "DTSTART;VALUE=DATE:" + DATE.format(day.date()));
    line(out, "DTEND;VALUE=DATE:" + DATE.format(day.date().plusDays(1)));
    line(out, "SUMMARY:" + escape(day.name()));
    line(out, "TRANSP:TRANSPARENT");
    day.findReadings().ifPresent(readings ->
      line(out, "DESCRIPTION:" + escape(description(readings))));
    line(out, "END:VEVENT");
  }

  /**
   * Stabilt UID härlett ur datum och namn. {@code String.hashCode} är
   * specificerad i språkdefinitionen och ger därför samma värde överallt.
   */
  private static String uid(Day day) {
    return "%s-%08x%s".formatted(DATE.format(day.date()), day.name().hashCode(), UID_DOMAIN);
  }

  private static String description(Readings readings) {
    StringBuilder text = new StringBuilder(readings.theme()).append('\n');
    append(text, "GT", readings.ot());
    append(text, "Ep", readings.ep());
    append(text, "Ev", readings.go());
    append(text, "Ps", readings.ps());
    append(text, "Alt", readings.alt());
    return text.toString();
  }

  private static void append(StringBuilder text, String label, Reading reading) {
    if (reading != null) {
      text.append(label).append(": ").append(reading.sweRef()).append('\n');
    }
  }

  /** Escaping av TEXT-värden enligt RFC 5545 avsnitt 3.3.11. */
  private static String escape(String value) {
    return value
      .replace("\\", "\\\\")
      .replace(";", "\\;")
      .replace(",", "\\,")
      .replace("\r\n", "\\n")
      .replace("\n", "\\n")
      .replace("\r", "\\n");
  }

  /**
   * Skriver en rad och viker den enligt RFC 5545 avsnitt 3.1.
   * <p>
   * Gränsen på 75 gäller oktetter, inte tecken, så vikningen räknar i UTF-8 och
   * bryter aldrig mitt i ett tecken.
   */
  private static void line(StringBuilder out, String content) {
    int octets = 0;
    for (int i = 0; i < content.length(); ) {
      int codePoint = content.codePointAt(i);
      int width = new String(Character.toChars(codePoint)).getBytes(StandardCharsets.UTF_8).length;
      // Vikta rader inleds med ett mellanslag, som också räknas.
      if (octets > 0 && octets + width > MAX_OCTETS_PER_LINE) {
        out.append(CRLF).append(' ');
        octets = 1;
      }
      out.appendCodePoint(codePoint);
      octets += width;
      i += Character.charCount(codePoint);
    }
    out.append(CRLF);
  }
}

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
import lectio.cal.Memorial;
import lectio.cal.Reading;
import lectio.cal.Readings;

import java.util.Collection;
import java.util.List;

/**
 * Kyrkoåret som JSON.
 * <p>
 * Den här klassen är enda källan till JSON-kontraktet: både kommandoraden och
 * webbtjänsten går via den, så formen kan inte glida isär mellan dem.
 *
 * <pre>{@code
 * {
 *   "day": "Fjärde söndagen efter trefaldighet",
 *   "date": "2020-07-05",
 *   "color": "GREEN",
 *   "memorials": [],
 *   "readings": {
 *     "theme": "Att inte döma",
 *     "ot": { "sweRef": "Hes 18:30-32", "enRef": "Ezek. 18:30-32", "text": "" },
 *     "ep": { ... }, "go": { ... }, "ps": { ... },
 *     "alt": null
 *   }
 * }
 * }</pre>
 *
 * @author marvi
 */
public final class JsonFormat {

  private static final String INDENT = "  ";

  private JsonFormat() {
  }

  /**
   * @param year kyrkoåret
   * @return kalendern som en JSON-lista
   */
  public static String forLiturgicalYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.LITURGICAL, year);
  }

  /**
   * @param year kalenderåret
   * @return kalendern som en JSON-lista
   */
  public static String forCalendarYear(int year) {
    return forYear(Formats.SHARED, CalendarBasis.CALENDAR, year);
  }

  /**
   * @param factory kalendern att hämta dagarna ur
   * @param basis   om årtalet syftar på kyrkoår eller kalenderår
   * @param year    årtalet
   * @return kalendern som en JSON-lista
   */
  public static String forYear(LiturgicalYearFactory factory, CalendarBasis basis, int year) {
    return forDays(basis.daysOf(factory, year));
  }

  /**
   * Skriver ut flera dagar.
   * <p>
   * Bibeltexten stryks alltid här. Ett helt år är aldrig den begränsade
   * mängd som får visas, och en bulkändpunkt är det enklaste sättet att av
   * misstag lämna ut hela evangelieboken. Enskilda dagar skrivs ut med
   * {@link #forDay}, där anroparen själv avgör.
   *
   * @param days dagarna att skriva ut, i den ordning de ska stå
   * @return dagarna som en JSON-lista, utan bibeltext
   */
  public static String forDays(Collection<Day> days) {
    StringBuilder out = new StringBuilder("[\n");
    int remaining = days.size();
    for (Day day : days) {
      appendDay(out, day.withoutText(), 1);
      out.append(--remaining > 0 ? ",\n" : "\n");
    }
    return out.append("]").toString();
  }

  /**
   * @param day dagen att skriva ut
   * @return dagen som ett JSON-objekt
   */
  public static String forDay(Day day) {
    StringBuilder out = new StringBuilder();
    appendDay(out, day, 0);
    return out.toString();
  }

  private static void appendDay(StringBuilder out, Day day, int depth) {
    String pad = INDENT.repeat(depth);
    String inner = INDENT.repeat(depth + 1);
    out.append(pad).append("{\n");
    out.append(inner).append(key("day")).append(string(day.name())).append(",\n");
    out.append(inner).append(key("date")).append(string(day.date().toString())).append(",\n");
    out.append(inner).append(key("color")).append(string(day.color().name())).append(",\n");
    out.append(inner).append(key("memorials"));
    appendMemorials(out, day.memorials(), depth + 1);
    out.append(",\n");
    out.append(inner).append(key("readings"));
    day.findReadings().ifPresentOrElse(
      readings -> appendReadings(out, readings, depth + 1),
      () -> out.append("null"));
    out.append("\n").append(pad).append("}");
  }

  private static void appendMemorials(StringBuilder out, List<Memorial> memorials, int depth) {
    if (memorials.isEmpty()) {
      out.append("[]");
      return;
    }
    String pad = INDENT.repeat(depth);
    String inner = INDENT.repeat(depth + 1);
    out.append("[\n");
    for (int i = 0; i < memorials.size(); i++) {
      Memorial memorial = memorials.get(i);
      out.append(inner).append("{ ")
        .append(key("name")).append(string(memorial.name())).append(", ")
        .append(key("yearOfDeath")).append(string(memorial.yearOfDeath())).append(", ")
        .append(key("description")).append(string(memorial.description()))
        .append(" }").append(i < memorials.size() - 1 ? ",\n" : "\n");
    }
    out.append(pad).append("]");
  }

  private static void appendReadings(StringBuilder out, Readings readings, int depth) {
    String pad = INDENT.repeat(depth);
    String inner = INDENT.repeat(depth + 1);
    out.append("{\n");
    out.append(inner).append(key("theme")).append(string(readings.theme())).append(",\n");
    appendReading(out, inner, "ot", readings.ot(), true);
    appendReading(out, inner, "ep", readings.ep(), true);
    appendReading(out, inner, "go", readings.go(), true);
    appendReading(out, inner, "ps", readings.ps(), true);
    appendReading(out, inner, "alt", readings.alt(), false);
    out.append(pad).append("}");
  }

  private static void appendReading(StringBuilder out, String indent, String name,
                                    Reading reading, boolean trailingComma) {
    out.append(indent).append(key(name));
    if (reading == null) {
      out.append("null");
    } else {
      out.append("{ ")
        .append(key("sweRef")).append(string(reading.sweRef())).append(", ")
        .append(key("enRef")).append(string(reading.enRef())).append(", ")
        .append(key("text")).append(string(reading.text()))
        .append(" }");
    }
    out.append(trailingComma ? ",\n" : "\n");
  }

  private static String key(String name) {
    return string(name) + ": ";
  }

  /** JSON-strängar enligt RFC 8259: styrtecken och citattecken måste kodas. */
  private static String string(String value) {
    if (value == null) {
      return "null";
    }
    StringBuilder out = new StringBuilder(value.length() + 2).append('"');
    for (int i = 0; i < value.length(); i++) {
      char c = value.charAt(i);
      switch (c) {
        case '"' -> out.append("\\\"");
        case '\\' -> out.append("\\\\");
        case '\b' -> out.append("\\b");
        case '\f' -> out.append("\\f");
        case '\n' -> out.append("\\n");
        case '\r' -> out.append("\\r");
        case '\t' -> out.append("\\t");
        default -> {
          if (c < 0x20) {
            out.append(String.format("\\u%04x", (int) c));
          } else {
            out.append(c);
          }
        }
      }
    }
    return out.append('"').toString();
  }
}

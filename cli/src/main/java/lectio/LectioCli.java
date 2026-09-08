/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio;

import lectio.cal.LiturgicalYear;
import lectio.cal.LiturgicalYearFactory;
import lectio.format.CalendarBasis;
import lectio.format.CsvFormat;
import lectio.format.IcalFormat;
import lectio.format.JsonFormat;
import lectio.format.TextFormat;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * Skriver ut Svenska kyrkans kyrkoår i valfritt format.
 *
 * @author marvi
 */
@Command(name = "lektionarium",
  mixinStandardHelpOptions = true,
  description = "Skapar en kalender över Svenska kyrkans kyrkoår.")
public class LectioCli implements Callable<Integer> {

  /** Utformat kalendern kan skrivas i. */
  enum Format {
    txt, json, csv, ical
  }

  @Parameters(index = "0", paramLabel = "ÅR",
    description = "Året att skapa kalender för, 2004 eller senare.")
  private int year;

  @Option(names = {"-f", "--format"}, paramLabel = "FORMAT",
    description = "Utformat: ${COMPLETION-CANDIDATES}. Standard: ${DEFAULT-VALUE}.")
  private Format format = Format.txt;

  @Option(names = {"-k", "--kalenderar"},
    description = "Tolka året som kalenderår i stället för kyrkoår.")
  private boolean calendarYear;

  @Option(names = {"-o", "--output"}, paramLabel = "FIL",
    description = "Skriv till fil i stället för standard ut.")
  private Path output;

  public static void main(String[] args) {
    System.exit(new CommandLine(new LectioCli()).execute(args));
  }

  @Override
  public Integer call() throws IOException {
    if (year < LiturgicalYear.FIRST_SUPPORTED_YEAR) {
      System.err.println("Endast år från och med " + LiturgicalYear.FIRST_SUPPORTED_YEAR
        + " stöds, fick " + year);
      return CommandLine.ExitCode.USAGE;
    }

    LiturgicalYearFactory calendar = new LiturgicalYearFactory();
    CalendarBasis basis = calendarYear ? CalendarBasis.CALENDAR : CalendarBasis.LITURGICAL;
    String result = switch (format) {
      case txt -> TextFormat.forYear(calendar, basis, year);
      case json -> JsonFormat.forYear(calendar, basis, year);
      case csv -> CsvFormat.forYear(calendar, basis, year);
      case ical -> IcalFormat.forYear(calendar, basis, year);
    };

    if (output != null) {
      Files.writeString(output, result, StandardCharsets.UTF_8);
    } else {
      // Kalenderdata är alltid UTF-8, oavsett vad skalet råkar ha för teckenkodning.
      PrintWriter out = new PrintWriter(System.out, true, StandardCharsets.UTF_8);
      out.print(result);
      out.flush();
    }
    return CommandLine.ExitCode.OK;
  }
}

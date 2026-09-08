package lectio.web;

import lectio.cal.Day;
import lectio.cal.Reading;
import lectio.cal.Readings;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * En dag färdig att visas i en mall. Mallarna ska bara skriva ut, inte räkna.
 *
 * @param name         dagens namn i kyrkoåret
 * @param isoDate      datum på formen 2026-04-05, för länkar och maskiner
 * @param humanDate    datum utskrivet på svenska, för läsaren
 * @param color        liturgisk färg, gemener, används som css-klass
 * @param colorName    liturgisk färg på svenska
 * @param theme        dagens tema, tom sträng om dagen saknar texter
 * @param readings     dagens läsningar, tom lista om dagen saknar texter
 * @param previousDate datum att länka bakåt till
 * @param nextDate     datum att länka framåt till
 * @author marvi
 */
public record DayView(String name, String isoDate, String humanDate, String color,
                      String colorName, String theme, List<ReadingView> readings,
                      String previousDate, String nextDate) {

  private static final DateTimeFormatter HUMAN =
    DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.of("sv", "SE"));

  /** En läsning färdig att visas. */
  public record ReadingView(String label, String sweRef, String enRef, String text) {

    /** Bibeltexten är bortstrippad ur den medföljande datafilen av upphovsrättsskäl. */
    public boolean hasText() {
      return text != null && !text.isBlank();
    }
  }

  public static DayView of(Day day, LocalDate previous, LocalDate next) {
    List<ReadingView> readings = new ArrayList<>();
    String theme = "";
    if (day.findReadings().isPresent()) {
      Readings source = day.findReadings().get();
      theme = source.theme();
      add(readings, "Gammaltestamentlig text", source.ot());
      add(readings, "Epistel", source.ep());
      add(readings, "Evangelium", source.go());
      add(readings, "Psaltarpsalm", source.ps());
      add(readings, "Alternativ text", source.alt());
    }
    return new DayView(
      day.name(),
      day.date().toString(),
      capitalize(HUMAN.format(day.date())),
      day.color().name().toLowerCase(Locale.ROOT),
      swedishColor(day.color().name()),
      theme,
      List.copyOf(readings),
      previous.toString(),
      next.toString());
  }

  public boolean hasReadings() {
    return !readings.isEmpty();
  }

  private static void add(List<ReadingView> readings, String label, Reading reading) {
    if (reading != null) {
      readings.add(new ReadingView(label, reading.sweRef(), reading.enRef(), reading.text()));
    }
  }

  private static String capitalize(String value) {
    return value.isEmpty() ? value
      : Character.toUpperCase(value.charAt(0)) + value.substring(1);
  }

  private static String swedishColor(String color) {
    return switch (color) {
      case "WHITE" -> "Vit";
      case "RED" -> "Röd";
      case "VIOLET" -> "Violett";
      case "BLUE" -> "Blå";
      case "BLACK" -> "Svart";
      case "GREEN" -> "Grön";
      case "PINK" -> "Rosa";
      default -> "Ospecificerad";
    };
  }
}

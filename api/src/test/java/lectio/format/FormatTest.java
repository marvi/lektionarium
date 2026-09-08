package lectio.format;

import lectio.cal.Day;
import lectio.cal.HolyDay;
import lectio.cal.LiturgicalColor;
import lectio.cal.LiturgicalYearFactory;
import lectio.cal.OrdinaryDay;
import lectio.cal.Reading;
import lectio.cal.Readings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marvi
 */
class FormatTest {

  private static final Instant STAMP = Instant.parse("2026-01-01T00:00:00Z");

  private static final Day PASKDAGEN = new HolyDay("Påskdagen", LocalDate.of(2026, 4, 5),
    LiturgicalColor.WHITE, new Readings("Kristus är uppstånden",
    new Reading("Jes 25:6-9", "Is. 25:6-9"),
    new Reading("1 Kor 15:1-8", "1Cor. 15:1-8"),
    new Reading("Matt 28:1-10", "Matt. 28:1-10"),
    new Reading("Ps 118:15-24", "Psa. 118:15-24"),
    null));

  private static final Day STILLA_ONSDAG = new OrdinaryDay("Onsdag i Stilla veckan",
    LocalDate.of(2026, 4, 1), LiturgicalColor.WHITE);

  private static final List<Day> DAYS = List.of(STILLA_ONSDAG, PASKDAGEN);

  @Nested
  class Csv {

    /** Regression: tidigare skrevs objektet i stället för bibelhänvisningen. */
    @Test
    @DisplayName("skriver bibelhänvisningen, inte objektets toString")
    void skriverBibelhanvisning() {
      String csv = CsvFormat.forDays(DAYS);
      assertTrue(csv.contains("Jes 25:6-9"), csv);
      assertFalse(csv.contains("Reading["), csv);
      assertFalse(csv.contains("@"), csv);
    }

    /** Regression: dagar utan läsningar gav tidigare kortare rader. */
    @Test
    @DisplayName("alla rader har lika många kolumner")
    void allaRaderHarSammaAntalKolumner() {
      String[] lines = CsvFormat.forDays(DAYS).split("\n");
      long columns = lines[0].chars().filter(c -> c == ';').count();
      for (String line : lines) {
        assertEquals(columns, line.chars().filter(c -> c == ';').count(), line);
      }
    }

    @Test
    void citerarFaltMedSemikolon() {
      Day tricky = new HolyDay("Dag; med semikolon", LocalDate.of(2026, 5, 1),
        LiturgicalColor.GREEN, new Readings("Tema \"citat\"",
        new Reading("A 1", "A 1"), new Reading("B 2", "B 2"),
        new Reading("C 3", "C 3"), new Reading("D 4", "D 4"), null));
      String csv = CsvFormat.forDays(List.of(tricky));
      assertTrue(csv.contains("\"Dag; med semikolon\""), csv);
      assertTrue(csv.contains("\"Tema \"\"citat\"\"\""), csv);
    }
  }

  @Nested
  class Json {

    /** Regression: org.json kunde inte introspektera records och gav [{},{}]. */
    @Test
    @DisplayName("skriver faktiskt ut dagens innehåll")
    void skriverUtInnehallet() {
      String json = JsonFormat.forDay(PASKDAGEN);
      assertTrue(json.contains("\"day\": \"Påskdagen\""), json);
      assertTrue(json.contains("\"date\": \"2026-04-05\""), json);
      assertTrue(json.contains("\"theme\": \"Kristus är uppstånden\""), json);
      assertTrue(json.contains("\"sweRef\": \"Jes 25:6-9\""), json);
      assertTrue(json.contains("\"alt\": null"), json);
    }

    @Test
    void dagUtanLasningarHarReadingsNull() {
      assertTrue(JsonFormat.forDay(STILLA_ONSDAG).contains("\"readings\": null"));
    }

    @Test
    void kodarCitattecken() {
      Day tricky = new OrdinaryDay("Han sa \"hej\"\n", LocalDate.of(2026, 5, 1),
        LiturgicalColor.GREEN);
      assertTrue(JsonFormat.forDay(tricky).contains("\\\"hej\\\"\\n"));
    }

    @Test
    void aretBlirEnLista() {
      String json = JsonFormat.forDays(DAYS);
      assertTrue(json.startsWith("["));
      assertTrue(json.endsWith("]"));
    }
  }

  @Nested
  class Ical {

    @Test
    void skriverGiltigtKuvert() {
      String ical = IcalFormat.forDays(DAYS, "Kyrkoåret 2026", STAMP);
      assertTrue(ical.startsWith("BEGIN:VCALENDAR\r\n"), ical);
      assertTrue(ical.endsWith("END:VCALENDAR\r\n"), ical);
      assertTrue(ical.contains("VERSION:2.0\r\n"));
      assertEquals(2, countOccurrences(ical, "BEGIN:VEVENT"));
    }

    @Test
    @DisplayName("dagarna blir heldagshändelser")
    void heldagshandelser() {
      String ical = IcalFormat.forDays(List.of(PASKDAGEN), "test", STAMP);
      assertTrue(ical.contains("DTSTART;VALUE=DATE:20260405"), ical);
      assertTrue(ical.contains("DTEND;VALUE=DATE:20260406"), ical);
    }

    /** Regression: RandomUidGenerator gav nya UID:n vid varje hämtning. */
    @Test
    @DisplayName("samma dag ger samma UID varje gång")
    void stabilaUid() {
      String first = IcalFormat.forDays(List.of(PASKDAGEN), "test", STAMP);
      String second = IcalFormat.forDays(List.of(PASKDAGEN), "test", STAMP);
      assertEquals(first, second);
      assertTrue(first.contains("@lektionarium.se"), first);
    }

    @Test
    void raderVikesVid75Oktetter() {
      String ical = IcalFormat.forDays(DAYS, "test", STAMP);
      for (String line : ical.split("\r\n")) {
        assertTrue(line.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 75,
          "för lång rad: " + line);
      }
    }

    @Test
    void vikningenGarAttFallaIhopIgen() {
      Day longName = new OrdinaryDay("A".repeat(200), LocalDate.of(2026, 5, 1),
        LiturgicalColor.GREEN);
      String ical = IcalFormat.forDays(List.of(longName), "test", STAMP);
      assertTrue(ical.replace("\r\n ", "").contains("SUMMARY:" + "A".repeat(200)));
    }

    @Test
    void escaparSpecialtecken() {
      Day tricky = new OrdinaryDay("Semikolon; komma, snedstreck\\", LocalDate.of(2026, 5, 1),
        LiturgicalColor.GREEN);
      String unfolded = IcalFormat.forDays(List.of(tricky), "test", STAMP).replace("\r\n ", "");
      assertTrue(unfolded.contains("SUMMARY:Semikolon\\; komma\\, snedstreck\\\\"), unfolded);
    }
  }

  @Nested
  class Prenumeration {

    @Test
    @DisplayName("flödet bär RFC 7986-uppgifterna klienten behöver")
    void barUppgifterForOmhamtning() {
      String ical = IcalFormat.forSubscription(DAYS, "Kyrkoåret",
        "https://lektionarium.se/ical", STAMP);
      assertTrue(ical.contains("SOURCE;VALUE=URI:https://lektionarium.se/ical"), ical);
      assertTrue(ical.contains("REFRESH-INTERVAL;VALUE=DURATION:P1W"), ical);
      assertTrue(ical.contains("NAME:Kyrkoåret"), ical);
      assertTrue(ical.contains("LAST-MODIFIED:"), ical);
      assertTrue(ical.contains("UID:kyrkoaret@lektionarium.se"), ical);
    }

    /** Utdatat måste vara identiskt mellan anrop, annars är ETag en lögn. */
    @Test
    @DisplayName("samma innehåll ger byte för byte samma svar")
    void arDeterministiskt() {
      assertEquals(
        IcalFormat.forSubscription(DAYS, "Kyrkoåret", "https://x/ical", STAMP),
        IcalFormat.forSubscription(DAYS, "Kyrkoåret", "https://x/ical", STAMP));
    }

    @Test
    void raderVikesAvenIFlodet() {
      String ical = IcalFormat.forSubscription(DAYS, "Kyrkoåret",
        "https://lektionarium.se/ical", STAMP);
      for (String line : ical.split("\r\n")) {
        assertTrue(line.getBytes(java.nio.charset.StandardCharsets.UTF_8).length <= 75,
          "för lång rad: " + line);
      }
    }
  }

  @Nested
  class Text {

    @Test
    void skriverDagOchLasningar() {
      String text = TextFormat.forDays(DAYS);
      assertTrue(text.contains("2026-04-05 Påskdagen"), text);
      assertTrue(text.contains("Gammaltestamentlig text: Jes 25:6-9"), text);
      assertTrue(text.contains("2026-04-01 Onsdag i Stilla veckan"), text);
    }
  }

  @Nested
  class Basis {

    private final LiturgicalYearFactory factory = new LiturgicalYearFactory();

    @Test
    @DisplayName("kyrkoår och kalenderår ger olika kalendrar")
    void kyrkoarSkiljerSigFranKalenderar() {
      String liturgical = TextFormat.forYear(factory, CalendarBasis.LITURGICAL, 2026);
      String calendar = TextFormat.forYear(factory, CalendarBasis.CALENDAR, 2026);
      assertTrue(liturgical.contains("2025-11-30 Första söndagen i advent"), "kyrkoåret börjar i advent");
      assertFalse(calendar.contains("2025-"), "kalenderåret innehåller bara 2026");
    }
  }

  private static int countOccurrences(String haystack, String needle) {
    int count = 0;
    for (int i = haystack.indexOf(needle); i >= 0; i = haystack.indexOf(needle, i + 1)) {
      count++;
    }
    return count;
  }
}

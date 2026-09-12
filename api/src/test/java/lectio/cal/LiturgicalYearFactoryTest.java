package lectio.cal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.SortedMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marvi
 */
class LiturgicalYearFactoryTest {

  private final LiturgicalYearFactory factory = new LiturgicalYearFactory();

  /** I kyrkoåret bor man kvar i den senast infallna dagen tills nästa infaller. */
  @ParameterizedTest(name = "{0} tillhör {1}")
  @CsvSource({
    "2026-04-05, Påskdagen",
    "2026-04-07, Annandag påsk",
    "2026-04-11, Annandag påsk",
    "2026-04-12, Andra söndagen i påsktiden",
    "2020-12-06, Andra söndagen i advent",
  })
  void hittarDagenViBefinnerOssI(LocalDate date, String expected) {
    assertEquals(expected, factory.getCurrentDay(date).name());
  }

  @ParameterizedTest(name = "föregående dag före {0} är {1}")
  @CsvSource({
    "2020-12-06, 2020-11-29",
    "2020-12-27, 2020-12-26",
    "2021-01-01, 2020-12-27",
    "2022-01-02, 2022-01-01",
    "2022-11-27, 2022-11-20",
  })
  void hittarForegaendeDag(LocalDate from, LocalDate expected) {
    assertEquals(expected, factory.getPreviousDay(from).date());
  }

  @ParameterizedTest(name = "nästa dag efter {0} är {1}")
  @CsvSource({
    "2020-11-29, 2020-12-06",
    "2020-12-26, 2020-12-27",
    "2025-12-28, 2026-01-01",
  })
  void hittarNastaDag(LocalDate from, LocalDate expected) {
    assertEquals(expected, factory.getNextDay(from).date());
  }

  @ParameterizedTest(name = "{0} tillhör kyrkoåret {1}")
  @CsvSource({
    "2020-11-29, 2021",
    "2024-11-29, 2024",
    "2026-09-29, 2026",
    "2021-04-03, 2021",
    "2023-12-03, 2024",
  })
  void hittarKyrkoaretForEttDatum(LocalDate date, int expected) {
    assertEquals(expected, factory.getLiturgicalYear(date).getYear());
  }

  @Test
  void kalenderaretInnehallerBaraSittEgetAr() {
    SortedMap<LocalDate, Day> days = factory.getDaysOfCalendarYear(2026);
    assertTrue(days.keySet().stream().allMatch(d -> d.getYear() == 2026));
    assertTrue(days.containsKey(LocalDate.of(2026, 1, 1)), "Nyårsdagen");
    assertTrue(days.containsKey(LocalDate.of(2026, 11, 29)), "Första söndagen i advent");
  }

  @Test
  void kalendermanadInnehallerBaraSinEgenManad() {
    SortedMap<LocalDate, Day> days = factory.getCalendarMonth(2026, 4);
    assertTrue(days.keySet().stream().allMatch(d -> d.getYear() == 2026 && d.getMonthValue() == 4));
    assertTrue(days.containsKey(LocalDate.of(2026, 4, 5)), "Påskdagen");
  }

  @Test
  void aterananvanderUtraknadeAr() {
    assertSame(factory.getYear(2026), factory.getYear(2026));
  }

  @Test
  void spannAvKyrkoarSlasIhop() {
    SortedMap<LocalDate, Day> window = factory.getDaysOfLiturgicalYears(2025, 2027);
    assertEquals(factory.getYear(2026).getDaysOfYear().get(LocalDate.of(2026, 4, 5)),
      window.get(LocalDate.of(2026, 4, 5)), "påskdagen 2026 ska finnas i fönstret");
    assertTrue(window.size() > factory.getYear(2026).getDaysOfYear().size(),
      "tre kyrkoår ska ge fler dagar än ett");
    assertThrows(UnsupportedOperationException.class, window::clear);
  }

  @Test
  void bakvantSpannAvvisas() {
    assertThrows(IllegalArgumentException.class,
      () -> factory.getDaysOfLiturgicalYears(2027, 2025));
  }

  @Test
  void kyrkoaretsBorjanArForstaAdvent() {
    assertEquals(LocalDate.of(2025, 11, 30), factory.startOfLiturgicalYear(2026));
  }

  /**
   * Cachen måste ha ett tak. Ett uträknat kyrkoår tar omkring 8 kB, så en
   * anropare som får välja årtal skulle annars kunna fylla minnet.
   */
  @Test
  @DisplayName("cachen växer inte obegränsat")
  void cachenHarEttTak() {
    LiturgicalYear first = factory.getYear(2004);
    assertSame(first, factory.getYear(2004), "ska återanvändas direkt");

    // Be om fler år än vad cachen rymmer.
    for (int year = 2100; year < 2300; year++) {
      factory.getYear(year);
    }

    assertNotSame(first, factory.getYear(2004),
      "2004 ska ha trängts ut och räknats om");
  }

  /**
   * Regressionstest. Metoderna som går fram och tillbaka i kalendern slog
   * tidigare ihop flera kyrkoår genom att skriva rakt in i den cachade kartan,
   * så ett år förorenades med dagar från grannåren vid varje anrop.
   */
  @Test
  @DisplayName("att bläddra i kalendern förorenar inte cachen")
  void bladdrandeForstorInteCachen() {
    LiturgicalYear year2021 = factory.getYear(2021);
    int before = year2021.getDaysOfYear().size();

    factory.getCurrentDay(LocalDate.of(2021, 6, 1));
    factory.getNextDay(LocalDate.of(2021, 6, 1));
    factory.getPreviousDay(LocalDate.of(2021, 6, 1));
    factory.getDaysOfCalendarYear(2021);
    factory.getCalendarMonth(2021, 6);

    assertEquals(before, factory.getYear(2021).getDaysOfYear().size(),
      "kyrkoåret ska innehålla exakt sina egna dagar");
    assertTrue(year2021.getDaysOfYear().keySet().stream()
        .allMatch(d -> d.getYear() == 2020 || d.getYear() == 2021),
      "kyrkoåret 2021 sträcker sig bara över advent 2020 till advent 2021");
  }
}

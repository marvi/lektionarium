package lectio.cal;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marvi
 */
class DayTest {

  private static final Readings READINGS = new Readings("Ett nådens år",
    new Reading("Sak 9:9-10", "Zech. 9:9-10"),
    new Reading("Rom 13:11-14", "Rom. 13:11-14"),
    new Reading("Matt 21:1-9", "Matt. 21:1-9"),
    new Reading("Ps 24", "Psa. 24"),
    null);

  @Test
  void ordnarDagarKronologiskt() {
    List<Day> days = new ArrayList<>(List.of(
      new OrdinaryDay("Gazonk", LocalDate.of(2010, 1, 2), LiturgicalColor.WHITE),
      new OrdinaryDay("Foo", LocalDate.of(1910, 1, 1), LiturgicalColor.WHITE),
      new OrdinaryDay("Bar", LocalDate.of(2010, 1, 1), LiturgicalColor.WHITE)));

    assertFalse(days.get(2).name().equals("Gazonk"));

    days.sort(null);
    assertEquals("Foo", days.get(0).name());
    assertEquals("Bar", days.get(1).name());
    assertEquals("Gazonk", days.get(2).name());
  }

  @Test
  void enVanligDagHarIngaLasningar() {
    Day day = new OrdinaryDay("Måndag i Stilla veckan", LocalDate.of(2026, 3, 30),
      LiturgicalColor.WHITE);
    assertTrue(day.findReadings().isEmpty());
    assertTrue(day.memorials().isEmpty());
  }

  @Test
  void enHelgdagHarLasningarOchTema() {
    HolyDay day = new HolyDay("Första söndagen i advent", LocalDate.of(2025, 11, 30),
      LiturgicalColor.WHITE, READINGS);
    assertEquals(READINGS, day.findReadings().orElseThrow());
    assertEquals("Ett nådens år", day.theme());
  }

  @Test
  void paskensDagarFoljerPaskserien() {
    assertTrue(HolyDay.usesEasterSeries("Långfredagen"));
    assertTrue(HolyDay.usesEasterSeries("Påskdagen"));
    assertFalse(HolyDay.usesEasterSeries("Midsommardagen"));
  }

  @Test
  void minnesdagarKanInteAndrasUtifran() {
    List<Memorial> memorials = new ArrayList<>();
    memorials.add(new Memorial("Basilius BL", "379", "Biskop av Caesarea i Kappadokien."));
    Day day = new OrdinaryDay("Nyårsdagen", LocalDate.of(2026, 1, 1),
      LiturgicalColor.WHITE, memorials);

    memorials.clear();
    assertEquals(1, day.memorials().size(), "dagen ska ha en egen kopia");
    assertThrows(UnsupportedOperationException.class, () -> day.memorials().clear());
  }
}

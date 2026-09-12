package lectio.cal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marvi
 */
class LectioRepositoryTest {

  @Test
  void laserInEvangelieboken() {
    ReadingCycles cycles = LectioRepository.getLectio();
    assertEquals(77, cycles.dayNames().size(), "evangelieboken har 77 dagar");
    assertTrue(cycles.hasReadings("Domssöndagen"));
    assertFalse(cycles.hasReadings("Onsdag i Stilla veckan"));
  }

  @Test
  void hittarLasningarForSerie() {
    Readings readings = LectioRepository.getLectio()
      .readingsFor("Domssöndagen", Cycle.FIRST).orElseThrow();
    assertEquals("Kristi återkomst", readings.theme());
    assertTrue(readings.go().sweRef().startsWith("Matt"));
  }

  @Test
  void paskensDagarHarEnFjardeSerie() {
    ReadingCycles cycles = LectioRepository.getLectio();
    assertTrue(cycles.readingsFor("Påskdagen", Cycle.FOURTH).isPresent());
    assertTrue(cycles.readingsFor("Midsommardagen", Cycle.FOURTH).isEmpty());
  }

  @Test
  void avvisarOkandSerie() {
    assertThrows(IllegalArgumentException.class, () -> Cycle.of(5));
  }

  /** Filen ändras aldrig, så den ska tolkas en enda gång. */
  @Test
  void laserBaraInFilenEnGang() {
    assertSame(LectioRepository.getLectio(), LectioRepository.getLectio());
  }
}

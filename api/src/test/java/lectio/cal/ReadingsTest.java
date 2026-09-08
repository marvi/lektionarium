package lectio.cal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Kontrollerar att rätt läsningsserie plockas fram för ett datum.
 *
 * @author marvi
 */
class ReadingsTest {

  private final LiturgicalYearFactory factory = new LiturgicalYearFactory();

  @ParameterizedTest(name = "gammaltestamentlig text {0} är {1}")
  @CsvSource({
    "2020-04-10, Jes 53:1-12",
    "2020-10-31, 5 Mos 34:1-5",
  })
  void hittarGammaltestamentligText(LocalDate date, String expected) {
    assertEquals(expected, readingsFor(date).ot().sweRef());
  }

  @ParameterizedTest(name = "epistel {0} är {1}")
  @CsvSource({
    "2023-02-22, 2 Kor 7:8-13",
    "2023-04-08, Ef 2:1−6",
    "2020-11-29, Rom 13:11-14",
  })
  void hittarEpistel(LocalDate date, String expected) {
    assertEquals(expected, readingsFor(date).ep().sweRef());
  }

  @ParameterizedTest(name = "evangelium {0} är {1}")
  @CsvSource({
    "2024-03-30, Matt 28:1-8",
    "2024-05-12, Joh 15:26-16:4",
  })
  void hittarEvangelium(LocalDate date, String expected) {
    assertEquals(expected, readingsFor(date).go().sweRef());
  }

  @Test
  void varjeHelgdagHarAllaFyraTexterna() {
    for (Day day : factory.getYear(2026).getDaysOfYear().values()) {
      day.findReadings().ifPresent(readings -> {
        assertNotNull(readings.theme(), day.name());
        assertNotNull(readings.ot(), day.name());
        assertNotNull(readings.ep(), day.name());
        assertNotNull(readings.go(), day.name());
        assertNotNull(readings.ps(), day.name());
      });
    }
  }

  private Readings readingsFor(LocalDate date) {
    return factory.getLiturgicalYear(date).getDaysOfYear().get(date)
      .findReadings().orElseThrow(() -> new AssertionError("inga läsningar för " + date));
  }
}

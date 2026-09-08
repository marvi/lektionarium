package lectio.cal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Facit i {@code paskdagar.csv} täcker 1875-2124 och kommer från
 * den gregorianska påskuträkningen.
 *
 * @author marvi
 */
class CalculateEasterTest {

  @ParameterizedTest(name = "påskdagen {0} är {1}")
  @CsvFileSource(resources = "/paskdagar.csv", numLinesToSkip = 2)
  @DisplayName("räknar ut påskdagen rätt för 250 år")
  void raknarUtPaskdagen(int year, LocalDate expected) {
    assertEquals(expected, CalculateEaster.forYear(year));
  }
}

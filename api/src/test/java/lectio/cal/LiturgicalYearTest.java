package lectio.cal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author marvi
 */
class LiturgicalYearTest {

  /** Dagar som infaller under kyrkoårets eget kalenderår. */
  @ParameterizedTest(name = "{0} är {1}")
  @CsvSource({
    "2010-03-28, Palmsöndagen",
    "2011-04-21, Skärtorsdagen",
    "2013-03-29, Långfredagen",
    "2022-04-17, Påskdagen",
    "2026-04-03, Långfredagen",
    "2012-04-09, Annandag påsk",
    "2011-01-02, Söndagen efter nyår",
    "2015-01-04, Söndagen efter nyår",
    "2012-01-06, Trettondedag jul",
    "2011-01-09, Första söndagen efter trettondedagen",
    "2019-01-13, Första söndagen efter trettondedagen",
    "2011-01-16, Andra söndagen efter trettondedagen",
    "2012-01-22, Tredje söndagen efter trettondedagen",
    "2011-02-13, Sjätte söndagen efter trettondedagen",
    "2057-02-11, Sjätte söndagen efter trettondedagen",
    "2011-03-27, Jungfru Marie bebådelsedag",
    "2012-03-25, Jungfru Marie bebådelsedag",
    "2013-03-17, Jungfru Marie bebådelsedag",
    "2012-02-05, Kyndelsmässodagen",
    "2014-02-02, Kyndelsmässodagen",
    "2013-02-03, Kyndelsmässodagen",
    "2012-02-12, Sexagesima",
    "2012-06-23, Midsommardagen",
    "2011-06-25, Midsommardagen",
    "2018-06-23, Midsommardagen",
    "2020-06-20, Midsommardagen",
    "2013-07-14, Kristi förklarings dag",
    "2008-07-06, Kristi förklarings dag",
    "2011-08-07, Kristi förklarings dag",
    "2011-10-09, Tacksägelsedagen",
    "2014-10-12, Tacksägelsedagen",
    "2024-10-13, Tacksägelsedagen",
    "2011-10-02, Den helige Mikaels dag",
  })
  void laggerUtDagarnaPaRattDatum(LocalDate date, String expected) {
    LiturgicalYear year = new LiturgicalYear(date.getYear());
    assertEquals(expected, year.getDaysOfYear().get(date).name());
  }

  /** Advent och jul hör till nästa kalenderårs kyrkoår. */
  @ParameterizedTest(name = "{0} är {1}")
  @CsvSource({
    "2013-12-29, Söndagen efter jul",
    "2020-12-27, Söndagen efter jul",
    "2015-11-29, Första söndagen i advent",
    "2014-11-30, Första söndagen i advent",
    "2024-12-29, Söndagen efter jul",
    "2023-12-24, Fjärde söndagen i advent",
    "2020-12-06, Andra söndagen i advent",
  })
  void adventOchJulHorTillNastaKyrkoar(LocalDate date, String expected) {
    LiturgicalYear year = new LiturgicalYear(date.getYear() + 1);
    assertEquals(expected, year.getDaysOfYear().get(date).name());
  }

  @Test
  void kyrkoaretBorjarForegaendeKalenderar() {
    assertTrue(new LiturgicalYear(2021).getDaysOfYear().containsKey(LocalDate.of(2020, 12, 6)));
  }

  @ParameterizedTest(name = "kyrkoåret {0} har läsningsserie {1}")
  @CsvSource({"2007,2", "2010,2", "2011,3", "2013,2", "2020,3", "2021,1"})
  void raknarUtLasningsserie(int year, int expected) {
    assertEquals(expected, new LiturgicalYear(year).getReadingCycle());
  }

  @ParameterizedTest(name = "kyrkoåret {0} har påskserie {1}")
  @CsvSource({"2004,1", "2008,1", "2009,2", "2010,3", "2011,4", "2012,1", "2020,1", "2021,2"})
  void raknarUtPaskserie(int year, int expected) {
    assertEquals(expected, LiturgicalYear.getEasterSeries(year));
  }

  @ParameterizedTest(name = "nästa {0} efter {1} är {2}")
  @CsvSource({
    "MONDAY,    1984-12-31, 1985-01-07",
    "TUESDAY,   1976-02-29, 1976-03-02",
    "WEDNESDAY, 2017-10-19, 2017-10-25",
    "THURSDAY,  2001-06-11, 2001-06-14",
    "FRIDAY,    2012-12-30, 2013-01-04",
    "SATURDAY,  2015-02-14, 2015-02-21",
    "SUNDAY,    1967-11-07, 1967-11-12",
  })
  void hittarNastaVeckodag(DayOfWeek dayOfWeek, LocalDate from, LocalDate expected) {
    assertEquals(expected, LiturgicalYear.nextWeekdayOfType(dayOfWeek, from));
  }

  @Test
  @DisplayName("år före 2004 avvisas, evangelieboken täcker dem inte")
  void avvisarArForeEvangelieboken() {
    assertThrows(IllegalArgumentException.class, () -> new LiturgicalYear(2003));
  }

  @Test
  void dagarnaGarInteAttAndraUtifran() {
    LiturgicalYear year = new LiturgicalYear(2026);
    assertThrows(UnsupportedOperationException.class, () -> year.getDaysOfYear().clear());
  }

  @Test
  void hittarHelgdagPaNamn() {
    LiturgicalYear year = new LiturgicalYear(2026);
    assertEquals(LocalDate.of(2026, 4, 5), year.findHolyDayByName("Påskdagen").orElseThrow().date());
    assertTrue(year.findHolyDayByName("Finns inte").isEmpty());
  }
}

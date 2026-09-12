/*
 * Copyright (c) 2010, 2026, marvi ab. All rights reserved.
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */
package lectio.cal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Reglerna för hur kyrkoårets dagar läggs ut, en regel per test.
 * <p>
 * Varje test pekar på ett år där regeln faktiskt slår till. Den breda
 * täckningen, alla dagar under femton år, finns i {@link LiturgicalYearReferenceTest}.
 *
 * @author marvi
 */
class LiturgicalYearTest {

  private static final Map<Integer, LiturgicalYear> YEARS = new HashMap<>();

  private static LiturgicalYear year(int year) {
    return YEARS.computeIfAbsent(year, LiturgicalYear::new);
  }

  /** Dagen på datumet, eller null om kyrkoåret inte har någon dag där. */
  private static Day dayOn(LocalDate date) {
    Day day = year(date.getYear()).getDaysOfYear().get(date);
    return day != null ? day : year(date.getYear() + 1).getDaysOfYear().get(date);
  }

  private static void assertDay(String expected, LocalDate date) {
    Day day = dayOn(date);
    assertEquals(expected, day == null ? null : day.name(), date.toString());
  }

  private static void assertNoDay(LocalDate date) {
    assertNull(dayOn(date), "ingen dag väntades " + date);
  }

  private static Set<String> namesIn(int year) {
    return year(year).getDaysOfYear().values().stream().map(Day::name).collect(Collectors.toSet());
  }

  private static void assertMissing(int year, String name) {
    assertFalse(namesIn(year).contains(name), name + " ska inte finnas kyrkoåret " + year);
  }

  @Nested
  class Ramar {

    @Test
    @DisplayName("kyrkoåret löper från första advent till dagen före nästa första advent")
    void kyrkoaretsGranser() {
      LiturgicalYear y = year(2021);
      assertEquals(LocalDate.of(2020, 11, 29), y.getDaysOfYear().firstKey());
      assertEquals("Första söndagen i advent", y.getDaysOfYear().firstEntry().getValue().name());
      assertTrue(y.getDaysOfYear().lastKey().isBefore(LocalDate.of(2021, 11, 28)),
        "första advent 2021 hör till nästa kyrkoår");
      assertEquals(2021, y.getYear());
    }

    @Test
    void paskdagenStyrAret() {
      assertEquals(LocalDate.of(2026, 4, 5), year(2026).getEasterDay());
    }

    @Test
    @DisplayName("år före 2004 avvisas, evangelieboken täcker dem inte")
    void avvisarArForeEvangelieboken() {
      assertThrows(IllegalArgumentException.class, () -> new LiturgicalYear(2003));
      assertEquals(2004, LiturgicalYear.FIRST_SUPPORTED_YEAR);
    }

    @Test
    void dagarnaGarInteAttAndraUtifran() {
      assertThrows(UnsupportedOperationException.class, () -> year(2026).getDaysOfYear().clear());
    }

    @Test
    void hittarHelgdagPaNamn() {
      assertEquals(LocalDate.of(2026, 4, 5), year(2026).findHolyDayByName("Påskdagen").orElseThrow().date());
      assertTrue(year(2026).findHolyDayByName("Finns inte").isEmpty());
    }

    @Test
    @DisplayName("varje dag i kyrkoåret har egna texter i evangelieboken")
    void allaDagarHarTexter() {
      year(2026).getDaysOfYear().values().forEach(day -> assertInstanceOf(HolyDay.class, day, day.name()));
    }
  }

  @Nested
  class JulOchNyar {

    @ParameterizedTest(name = "{1} är {0}")
    @CsvSource({
      "Julnatten,        2022-12-24",
      "Juldagen,         2022-12-25",
      "Annandag jul,     2022-12-26",
      "Nyårsdagen,       2023-01-01",
      "Trettondedag jul, 2023-01-06",
    })
    void fastaDagar(String expected, LocalDate date) {
      assertDay(expected, date);
    }

    @Test
    @DisplayName("söndagen efter jul finns bara om en söndag ryms 27-31 december")
    void sondagenEfterJul() {
      assertDay("Söndagen efter jul", LocalDate.of(2008, 12, 28));
      assertDay("Söndagen efter jul", LocalDate.of(2013, 12, 29));
      // Juldagen 2022 är en söndag, nästa söndag är nyårsdagen.
      assertMissing(2023, "Söndagen efter jul");
    }

    @Test
    @DisplayName("söndagen efter nyår finns bara om en söndag ryms 2-5 januari")
    void sondagenEfterNyar() {
      assertDay("Söndagen efter nyår", LocalDate.of(2011, 1, 2));
      assertDay("Söndagen efter nyår", LocalDate.of(2015, 1, 4));
      // Nyårsdagen 2023 är en söndag, nästa söndag är den 8:e.
      assertMissing(2023, "Söndagen efter nyår");
      // 2030 infaller söndagen på trettondedagen.
      assertMissing(2030, "Söndagen efter nyår");
    }
  }

  @Nested
  class Trettondedagstiden {

    @Test
    @DisplayName("första söndagen efter trettondedagen är söndagen efter 6 januari, även när den 6:e är en söndag")
    void forstaSondagenEfterTrettondedagen() {
      assertDay("Första söndagen efter trettondedagen", LocalDate.of(2011, 1, 9));
      assertDay("Trettondedag jul", LocalDate.of(2019, 1, 6));
      assertDay("Första söndagen efter trettondedagen", LocalDate.of(2019, 1, 13));
    }

    @Test
    @DisplayName("antalet söndagar efter trettondedagen beror på när påsken infaller")
    void antalSondagarStyrsAvPasken() {
      assertDay("Sjätte söndagen efter trettondedagen", LocalDate.of(2011, 2, 13));
      assertDay("Sjätte söndagen efter trettondedagen", LocalDate.of(2057, 2, 11));
      // Tidig påsk 2008 (23 mars): bara en söndag innan Septuagesima.
      assertDay("Första söndagen efter trettondedagen", LocalDate.of(2008, 1, 13));
      assertDay("Septuagesima", LocalDate.of(2008, 1, 20));
    }

    @Test
    @DisplayName("Kyndelsmässodagen är söndagen 2-8 februari")
    void kyndelsmassodagen() {
      assertDay("Kyndelsmässodagen", LocalDate.of(2014, 2, 2));
      assertDay("Kyndelsmässodagen", LocalDate.of(2012, 2, 5));
      assertDay("Kyndelsmässodagen", LocalDate.of(2026, 2, 8));
    }

    @Test
    @DisplayName("Kyndelsmässodagen flyttas en vecka bakåt om den krockar med Fastlagssöndagen")
    void kyndelsmassodagenVikerForFastlagssondagen() {
      assertDay("Kyndelsmässodagen", LocalDate.of(2008, 1, 27));
      assertDay("Fastlagssöndagen", LocalDate.of(2008, 2, 3));
    }

    @Test
    @DisplayName("numreringen fortsätter förbi Kyndelsmässodagen")
    void numreringenHopparOverKyndelsmassodagen() {
      assertDay("Tredje söndagen efter trettondedagen", LocalDate.of(2030, 1, 27));
      assertDay("Kyndelsmässodagen", LocalDate.of(2030, 2, 3));
      assertDay("Femte söndagen efter trettondedagen", LocalDate.of(2030, 2, 10));
      assertMissing(2030, "Fjärde söndagen efter trettondedagen");
    }
  }

  @Nested
  class Fastan {

    @ParameterizedTest(name = "{0} infaller {1} dagar före påskdagen")
    @CsvSource({
      "Septuagesima,              63",
      "Sexagesima,                56",
      "Fastlagssöndagen,          49",
      "Askonsdagen,               46",
      "Första söndagen i fastan,  42",
      "Andra söndagen i fastan,   35",
      "Midfastosöndagen,          21",
      "Femte söndagen i fastan,   14",
      "Palmsöndagen,               7",
    })
    void raknasFranPasken(String expected, int daysBefore) {
      // 2030 har sen påsk (21 april) så Kyndelsmässodagen krockar inte; bebådelsedagen
      // hamnar alltid på någon fastesöndag, 2030 på den tredje.
      assertDay(expected, year(2030).getEasterDay().minusDays(daysBefore));
    }

    @Test
    void tredjeSondagenIFastan() {
      assertDay("Tredje söndagen i fastan", year(2027).getEasterDay().minusDays(28));
    }

    @Test
    @DisplayName("Kyndelsmässodagen tränger undan Septuagesima eller Sexagesima")
    void kyndelsmassodagenTrangerUndan() {
      assertDay("Kyndelsmässodagen", LocalDate.of(2023, 2, 5));
      assertMissing(2023, "Septuagesima");
      assertDay("Kyndelsmässodagen", LocalDate.of(2027, 1, 31));
      assertMissing(2027, "Sexagesima");
    }

    @Test
    @DisplayName("Jungfru Marie bebådelsedag är söndagen 22-28 mars")
    void bebadelsedagen() {
      assertDay("Jungfru Marie bebådelsedag", LocalDate.of(2011, 3, 27));
      assertDay("Jungfru Marie bebådelsedag", LocalDate.of(2012, 3, 25));
      assertDay("Jungfru Marie bebådelsedag", LocalDate.of(2026, 3, 22));
    }

    @Test
    @DisplayName("bebådelsedagen flyttas till söndagen före palmsöndagen om den skulle hamna i stilla veckan")
    void bebadelsedagenVikerForPalmsondagen() {
      assertDay("Jungfru Marie bebådelsedag", LocalDate.of(2013, 3, 17));
      assertDay("Palmsöndagen", LocalDate.of(2013, 3, 24));
      // Tidigast möjliga: påsk 23 mars 2008.
      assertDay("Jungfru Marie bebådelsedag", LocalDate.of(2008, 3, 9));
    }

    @Test
    @DisplayName("bebådelsedagen tränger undan fastesöndagen den hamnar på")
    void bebadelsedagenTrangerUndanFastesondag() {
      assertDay("Jungfru Marie bebådelsedag", LocalDate.of(2027, 3, 14));
      assertMissing(2027, "Femte söndagen i fastan");
      assertDay("Jungfru Marie bebådelsedag", LocalDate.of(2031, 3, 23));
      assertMissing(2031, "Midfastosöndagen");
    }
  }

  @Nested
  class PaskOchPingst {

    @ParameterizedTest(name = "{0} infaller {1} dagar efter påskdagen")
    @CsvSource({
      "Palmsöndagen,               -7",
      "Skärtorsdagen,              -3",
      "Långfredagen,               -2",
      "Påsknatten,                 -1",
      "Påskdagen,                   0",
      "Annandag påsk,               1",
      "Andra söndagen i påsktiden,  7",
      "Tredje söndagen i påsktiden, 14",
      "Fjärde söndagen i påsktiden, 21",
      "Femte söndagen i påsktiden,  28",
      "Bönsöndagen,                 35",
      "Kristi himmelsfärds dag,     39",
      "Söndagen före pingst,        42",
      "Pingstdagen,                 49",
      "Annandag pingst,             50",
      "Heliga trefaldighets dag,    56",
    })
    void raknasFranPasken(String expected, int daysAfter) {
      assertDay(expected, year(2026).getEasterDay().plusDays(daysAfter));
    }

    @Test
    void kristiHimmelsfardsDagArEnTorsdag() {
      assertEquals(DayOfWeek.THURSDAY,
        year(2026).findHolyDayByName("Kristi himmelsfärds dag").orElseThrow().date().getDayOfWeek());
    }
  }

  @Nested
  class Trefaldighetstiden {

    @Test
    @DisplayName("femte och sjunde söndagen efter trefaldighet har egna namn")
    void namngivnaSondagar() {
      assertDay("Fjärde söndagen efter trefaldighet", LocalDate.of(2026, 6, 28));
      assertDay("Apostladagen", LocalDate.of(2026, 7, 5));
      assertDay("Sjätte söndagen efter trefaldighet", LocalDate.of(2026, 7, 12));
      assertDay("Kristi förklarings dag", LocalDate.of(2026, 7, 19));
      assertDay("Åttonde söndagen efter trefaldighet", LocalDate.of(2026, 7, 26));
    }

    @Test
    @DisplayName("Midsommardagen är lördagen 20-26 juni och Johannes Döparens dag söndagen efter")
    void midsommar() {
      assertDay("Midsommardagen", LocalDate.of(2020, 6, 20));
      assertDay("Midsommardagen", LocalDate.of(2011, 6, 25));
      assertDay("Den helige Johannes Döparens dag", LocalDate.of(2026, 6, 21));
    }

    @Test
    @DisplayName("Johannes Döparens dag tränger undan söndagen efter trefaldighet, även Apostladagen")
    void johannesDoparensDagTrangerUndan() {
      assertMissing(2026, "Tredje söndagen efter trefaldighet");
      assertDay("Den helige Johannes Döparens dag", LocalDate.of(2027, 6, 27));
      assertMissing(2027, "Apostladagen");
    }

    @Test
    @DisplayName("Mikaelidagen är söndagen 29 september-5 oktober och Tacksägelsedagen söndagen efter")
    void mikaeliOchTacksagelse() {
      assertDay("Den helige Mikaels dag", LocalDate.of(2011, 10, 2));
      assertDay("Den helige Mikaels dag", LocalDate.of(2008, 10, 5));
      assertDay("Tacksägelsedagen", LocalDate.of(2008, 10, 12));
      assertDay("Tacksägelsedagen", LocalDate.of(2024, 10, 13));
    }

    @Test
    @DisplayName("numreringen fortsätter förbi de namngivna dagarna")
    void numreringenHopparOverNamngivnaDagar() {
      assertDay("Nittonde söndagen efter trefaldighet", LocalDate.of(2008, 9, 28));
      assertDay("Tjugoandra söndagen efter trefaldighet", LocalDate.of(2008, 10, 19));
      assertDay("Tjugofemte söndagen efter trefaldighet", LocalDate.of(2008, 11, 9));
      assertMissing(2008, "Tjugonde söndagen efter trefaldighet");
      assertMissing(2008, "Tjugofjärde söndagen efter trefaldighet");
    }

    @Test
    @DisplayName("Alla helgons dag är lördagen 31 oktober-6 november, med en egen söndag efter")
    void allaHelgonsDag() {
      assertDay("Alla helgons dag", LocalDate.of(2008, 11, 1));
      assertDay("Söndagen efter alla helgons dag", LocalDate.of(2008, 11, 2));
      assertDay("Alla helgons dag", LocalDate.of(2038, 11, 6));
      assertDay("Söndagen efter alla helgons dag", LocalDate.of(2038, 11, 7));
    }

    @Test
    @DisplayName("kyrkoåret slutar med söndagen före domssöndagen och domssöndagen")
    void aretsSlut() {
      assertDay("Söndagen före domssöndagen", LocalDate.of(2008, 11, 16));
      assertDay("Domssöndagen", LocalDate.of(2008, 11, 23));
      assertDay("Första söndagen i advent", LocalDate.of(2008, 11, 30));
      // Sen påsk 2038: trefaldighetstiden tar slut redan före alla helgons dag.
      assertDay("Nittonde söndagen efter trefaldighet", LocalDate.of(2038, 10, 31));
      assertDay("Söndagen före domssöndagen", LocalDate.of(2038, 11, 14));
      assertMissing(2038, "Tjugonde söndagen efter trefaldighet");
    }
  }

  @Nested
  class Advent {

    @Test
    @DisplayName("första advent är söndagen 27 november-3 december och inleder nästa kyrkoår")
    void forstaAdvent() {
      assertDay("Första söndagen i advent", LocalDate.of(2015, 11, 29));
      assertDay("Första söndagen i advent", LocalDate.of(2014, 11, 30));
      assertDay("Första söndagen i advent", LocalDate.of(2023, 12, 3));
      assertEquals(2024, year(2024).getYear());
      assertTrue(year(2024).getDaysOfYear().containsKey(LocalDate.of(2023, 12, 3)));
    }

    @Test
    void adventsSondagarnaFoljerVeckovis() {
      assertDay("Andra söndagen i advent", LocalDate.of(2020, 12, 6));
      assertDay("Tredje söndagen i advent", LocalDate.of(2020, 12, 13));
      assertDay("Fjärde söndagen i advent", LocalDate.of(2020, 12, 20));
    }

    /**
     * När julafton är en söndag firas både fjärde advent och julnatten. Modellen
     * rymmer bara en dag per datum och Julnatten faller bort. Se issue #17.
     */
    @Test
    @DisplayName("fjärde advent på julafton tränger tills vidare undan Julnatten")
    void fjardeAdventPaJulafton() {
      assertDay("Fjärde söndagen i advent", LocalDate.of(2023, 12, 24));
      assertMissing(2024, "Julnatten");
    }
  }

  @Nested
  class Farger {

    @ParameterizedTest(name = "{1} är {2}")
    @CsvSource({
      "WHITE,  2025-11-30, Första söndagen i advent",
      "VIOLET, 2025-12-07, Andra söndagen i advent",
      "GREEN,  2026-01-11, Första söndagen efter trettondedagen",
      "VIOLET, 2026-02-01, Septuagesima",
      "WHITE,  2026-03-29, Palmsöndagen",
      "RED,    2026-05-24, Pingstdagen",
      "RED,    2026-05-25, Annandag pingst",
      "GREEN,  2026-06-20, Midsommardagen",
      "RED,    2026-06-21, Den helige Johannes Döparens dag",
      "RED,    2026-07-05, Apostladagen",
      "WHITE,  2026-07-19, Kristi förklarings dag",
      "GREEN,  2026-07-26, Åttonde söndagen efter trefaldighet",
      "WHITE,  2026-10-31, Alla helgons dag",
      "GREEN,  2026-11-22, Domssöndagen",
    })
    void liturgiskFarg(LiturgicalColor expected, LocalDate date, String name) {
      Day day = dayOn(date);
      assertEquals(name, day.name());
      assertEquals(expected, day.color());
    }
  }

  @Nested
  class Serier {

    @ParameterizedTest(name = "kyrkoåret {0} har läsningsserie {1}")
    @CsvSource({"2004,SECOND", "2007,SECOND", "2010,SECOND", "2011,THIRD", "2013,SECOND", "2020,THIRD", "2021,FIRST"})
    void lasningsserieLoperITreArsCykler(int year, Cycle expected) {
      assertEquals(expected, Cycle.readingCycleOf(year));
    }

    @ParameterizedTest(name = "kyrkoåret {0} har påskserie {1}")
    @CsvSource({"2004,FIRST", "2008,FIRST", "2009,SECOND", "2010,THIRD", "2011,FOURTH", "2012,FIRST", "2020,FIRST", "2021,SECOND"})
    void paskserieLoperIFyraArsCykler(int year, Cycle expected) {
      assertEquals(expected, Cycle.easterSeriesOf(year));
    }

    @Test
    void serierFinnsBaraForStoddaAr() {
      assertThrows(IllegalArgumentException.class, () -> Cycle.readingCycleOf(2003));
      assertThrows(IllegalArgumentException.class, () -> Cycle.easterSeriesOf(2003));
      assertEquals(Cycle.FOURTH, Cycle.of(4));
      assertThrows(IllegalArgumentException.class, () -> Cycle.of(5));
    }

    @Test
    void aretBarSinaSerier() {
      assertEquals(Cycle.THIRD, year(2026).getReadingCycle());
      assertEquals(Cycle.THIRD, year(2026).getEasterSeries());
    }

    @Test
    @DisplayName("stilla veckan och påskdagen hämtar texter ur påskserien, övriga dagar ur läsningsserien")
    void paskensDagarFoljerPaskserien() {
      ReadingCycles lectio = LectioRepository.getLectio();
      LiturgicalYear y = year(2007);
      assertEquals(Cycle.FOURTH, y.getEasterSeries());
      assertEquals(Cycle.SECOND, y.getReadingCycle());
      assertEquals(lectio.readingsFor("Påskdagen", Cycle.FOURTH).orElseThrow(),
        y.findHolyDayByName("Påskdagen").orElseThrow().readings());
      assertEquals(lectio.readingsFor("Pingstdagen", Cycle.SECOND).orElseThrow(),
        y.findHolyDayByName("Pingstdagen").orElseThrow().readings());
    }
  }
}

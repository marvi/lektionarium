package lectio.cal;

import lectio.format.JsonFormat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Bibeltexten är upphovsrättsskyddad och får bara återges i begränsad
 * omfattning. Den här klassen kontrollerar att bortredigeringen fungerar.
 *
 * @author marvi
 */
class BibleTextTest {

  private static final Reading MED_TEXT =
    new Reading("Jes 25:6-9", "Is. 25:6-9", "PLATSHALLARE");
  private static final Readings TEXTER = new Readings("Kristus är uppstånden",
    MED_TEXT,
    new Reading("1 Kor 15:1-8", "1Cor. 15:1-8", "PLATSHALLARE"),
    new Reading("Matt 28:1-10", "Matt. 28:1-10", "PLATSHALLARE"),
    new Reading("Ps 118:15-24", "Psa. 118:15-24", "PLATSHALLARE"),
    null);

  @Test
  void lasningUtanTextArOforandrad() {
    Reading utan = new Reading("Jes 25:6-9", "Is. 25:6-9");
    assertFalse(utan.hasText());
    assertSame(utan, utan.withoutText(), "ingen onödig kopia");
  }

  @Test
  void strykerTextenMenBevararHanvisningen() {
    Reading strippad = MED_TEXT.withoutText();
    assertFalse(strippad.hasText());
    assertEquals("Jes 25:6-9", strippad.sweRef());
    assertEquals("Is. 25:6-9", strippad.enRef());
  }

  @Test
  void strykerAllaLasningarPaEnGang() {
    assertTrue(TEXTER.hasText());
    Readings strippade = TEXTER.withoutText();
    assertFalse(strippade.hasText());
    assertEquals("Kristus är uppstånden", strippade.theme(), "temat är inte bibeltext");
    assertEquals("Jes 25:6-9", strippade.ot().sweRef());
    assertEquals("1 Kor 15:1-8", strippade.ep().sweRef());
    assertEquals("Matt 28:1-10", strippade.go().sweRef());
    assertEquals("Ps 118:15-24", strippade.ps().sweRef());
    assertNull(strippade.alt(), "en läsning som saknas ska fortsätta saknas");
  }

  @Test
  void helgdagKanLamnaIfranSigEnTextfriKopia() {
    Day med = new HolyDay("Påskdagen", LocalDate.of(2026, 4, 5),
      LiturgicalColor.WHITE, List.of(), TEXTER);
    assertTrue(med.hasText());

    Day utan = med.withoutText();
    assertFalse(utan.hasText());
    assertEquals(med.name(), utan.name());
    assertEquals(med.date(), utan.date());
    assertEquals(med.color(), utan.color());
    Readings kvar = utan.findReadings().orElseThrow();
    assertEquals("Kristus är uppstånden", kvar.theme());
    assertEquals("Matt 28:1-10", kvar.go().sweRef(), "hänvisningen ska vara kvar");
    assertEquals("", kvar.go().text(), "men inte texten");
  }

  @Test
  void vanligDagPaverkasInte() {
    Day dag = new OrdinaryDay("Onsdag i Stilla veckan", LocalDate.of(2026, 4, 1),
      LiturgicalColor.WHITE);
    assertSame(dag, dag.withoutText());
    assertFalse(dag.hasText());
  }

  /** Ett helt år är aldrig den begränsade mängd som får återges. */
  @Test
  @DisplayName("bulkexport i JSON lämnar aldrig ut bibeltext")
  void bulkexportStryckerAlltid() {
    Day med = new HolyDay("Påskdagen", LocalDate.of(2026, 4, 5),
      LiturgicalColor.WHITE, List.of(), TEXTER);

    assertTrue(JsonFormat.forDay(med).contains("PLATSHALLARE"), "enskild dag styr anroparen");
    assertFalse(JsonFormat.forDays(List.of(med)).contains("PLATSHALLARE"),
      "flera dagar stryks alltid");
  }

  /**
   * XML-filen är radbruten och indragen för att vara läsbar som fil. Det är
   * formatering av dokumentet, inte av texten, och ska inte följa med ut.
   */
  @Test
  @DisplayName("filens radbrytning och indrag följer inte med texten")
  void filensFormateringFoljerInteMed() throws Exception {
    Path fixtur = Path.of(BibleTextTest.class
      .getResource("/testfixtur-indenterad.xml").toURI());
    Reading ot = LectioRepository.load(fixtur)
      .readingsFor("Domssöndagen", 1).orElseThrow().ot();

    assertEquals("PLATSHALLARE rad ett PLATSHALLARE rad tva PLATSHALLARE rad tre",
      ot.text(), "radbrytningar och indrag ska bli enkla mellanslag");
    assertFalse(ot.text().contains("\n"));
    assertFalse(ot.text().contains("  "));
    assertEquals(ot.text().strip(), ot.text(), "inga kantblanksteg");
  }

  @Test
  void medfoljandeEvangeliebokHarIngenText() {
    assertFalse(LectioRepository.getLectio().containsBibleText(),
      "filen i repot ska ha texten bortstrippad");
    assertFalse(new LiturgicalYearFactory().hasBibleText());
  }
}

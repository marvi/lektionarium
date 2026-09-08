package lectio.web;

import lectio.cal.Day;
import lectio.cal.LectioRepository;
import lectio.cal.LiturgicalYearFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Fixturen innehåller påhittad platshållartext, inte bibeltext.
 *
 * @author marvi
 */
class BibleTextPolicyTest {

  /** Påskdagen 2026. Fixturen har texter för just den dagen. */
  private static final LocalDate PASKDAGEN = LocalDate.of(2026, 4, 5);

  private final LiturgicalYearFactory medText = new LiturgicalYearFactory(
    LectioRepository.load(fixture()));
  private final BibleTextPolicy policy = new BibleTextPolicy(medText, 3);

  private static Path fixture() {
    try {
      return Path.of(BibleTextPolicyTest.class
        .getResource("/testfixtur-platshallartext.xml").toURI());
    } catch (URISyntaxException ex) {
      throw new IllegalStateException(ex);
    }
  }

  @Test
  void kannerIgenEnEvangeliebokMedText() {
    assertTrue(medText.hasBibleText());
    assertFalse(new LiturgicalYearFactory().hasBibleText(),
      "den medföljande filen har texten bortstrippad");
  }

  @Test
  @DisplayName("fönstret är dagen vi är i plus två")
  void fonstretArTreDagar() {
    Set<LocalDate> allowed = policy.allowedDates(PASKDAGEN);
    assertEquals(3, allowed.size(), allowed.toString());
    assertTrue(allowed.contains(PASKDAGEN), "dagen vi befinner oss i");
    assertTrue(allowed.contains(LocalDate.of(2026, 4, 6)), "annandag påsk");
    assertTrue(allowed.contains(LocalDate.of(2026, 4, 12)), "andra söndagen i påsktiden");
  }

  @Test
  void fonstretGarAttSnavaAt() {
    assertEquals(1, new BibleTextPolicy(medText, 1).allowedDates(PASKDAGEN).size());
  }

  @Test
  void textenFoljerMedInomFonstret() {
    Day paskdagen = medText.getCurrentDay(PASKDAGEN);
    assertTrue(paskdagen.hasText(), "fixturen har texter för påskdagen");
    assertTrue(policy.redact(paskdagen, PASKDAGEN).hasText());
  }

  @Test
  @DisplayName("texten stryks för en dag utanför fönstret")
  void textenStryksUtanforFonstret() {
    Day paskdagen = medText.getCurrentDay(PASKDAGEN);

    // Samma dag, men betraktad ett halvår senare: då ligger den långt utanför.
    Day redigerad = policy.redact(paskdagen, LocalDate.of(2026, 10, 4));

    assertFalse(redigerad.hasText(), "bibeltexten ska vara borta");
    assertEquals("Matt 28:1-10",
      redigerad.findReadings().orElseThrow().go().sweRef(),
      "men hänvisningen ska vara kvar");
  }

  /** Utan en evangeliebok med text är hela mekaniken verkningslös. */
  @Test
  void utanTextfilVisasAldrigNagonText() {
    BibleTextPolicy utanText = new BibleTextPolicy(new LiturgicalYearFactory(), 3);
    assertFalse(utanText.mayShowText(PASKDAGEN, PASKDAGEN));
  }
}

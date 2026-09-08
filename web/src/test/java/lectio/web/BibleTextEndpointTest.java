package lectio.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Kontrollerar att bibeltexten inte läcker ut genom ändpunkter som får cachas
 * länge eller som lämnar ut stora mängder på en gång.
 * <p>
 * Applikationen startas med en fixtur som bär påhittad platshållartext, inte
 * bibeltext. Läcker mekaniken syns det som att PLATSHALLARE dyker upp i svar
 * där den inte hör hemma.
 *
 * @author marvi
 */
@SpringBootTest(properties =
  "lektionarium.lectionary-file=src/test/resources/testfixtur-platshallartext.xml")
class BibleTextEndpointTest {

  private static final String TEXT = "PLATSHALLARE";

  private MockMvc mvc;

  @Autowired
  void setUp(WebApplicationContext context) {
    this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  /**
   * De datumstyrda ändpunkterna svarar immutable i trettio dagar och kan
   * anropas för vilket datum som helst. Skulle de bära text vore de ett sätt
   * att hämta hem hela evangelieboken.
   */
  @Test
  @DisplayName("datumstyrda ändpunkter lämnar aldrig ut bibeltext")
  void datumstyrdaAndpunkterLackerInte() throws Exception {
    for (String url : new String[]{
      "/day/2026-04-05", "/next/2026-04-04", "/previous/2026-04-06"}) {
      assertFalse(body(url).contains(TEXT), url + " lämnade ut bibeltext");
    }
  }

  /** Ett helt år är aldrig den begränsade mängd som får återges. */
  @Test
  @DisplayName("årsändpunkterna lämnar aldrig ut bibeltext")
  void arsandpunkterLackerInte() throws Exception {
    for (String url : new String[]{
      "/json/2026", "/csv/2026", "/txt/2026", "/ical/2026", "/ical"}) {
      assertFalse(body(url).contains(TEXT), url + " lämnade ut bibeltext");
    }
  }

  /** Sidan för en enskild dag ligger nästan alltid utanför fönstret. */
  @Test
  void gamlaSidorVisarIngenText() throws Exception {
    assertFalse(body("/dag/2026-04-05").contains(TEXT),
      "påskdagen 2026 ligger utanför fönstret så länge det inte är påsk");
  }

  private String body(String url) throws Exception {
    return mvc.perform(get(url)).andReturn().getResponse().getContentAsString();
  }
}

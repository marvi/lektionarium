package lectio;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import lectio.cal.LiturgicalYearFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author marvi
 */
@SpringBootTest
class LectioApplicationTests {

  private MockMvc mvc;

  @Autowired
  void setUp(WebApplicationContext context) {
    this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  void startar() {
  }

  @Test
  void startsidanRenderasPaServern() throws Exception {
    mvc.perform(get("/"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith("text/html"))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"dagkort\"")))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("Lektionarium")));
  }

  @Test
  void enskildDagHarNamnOchLasningar() throws Exception {
    mvc.perform(get("/dag/2026-04-05"))
      .andExpect(status().isOk())
      .andExpect(content().string(org.hamcrest.Matchers.containsString("Påskdagen")))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("Liturgisk färg")));
  }

  /** Ett htmx-anrop ska bara få kortet, inte hela sidan. */
  @Test
  void htmxAnropGerBaraFragmentet() throws Exception {
    mvc.perform(get("/dag/2026-04-05").header("HX-Request", "true"))
      .andExpect(status().isOk())
      .andExpect(content().string(org.hamcrest.Matchers.containsString("id=\"dagkort\"")))
      .andExpect(content().string(org.hamcrest.Matchers.not(
        org.hamcrest.Matchers.containsString("<!DOCTYPE html>"))));
  }

  @Test
  void dagensJsonFoljerKontraktet() throws Exception {
    mvc.perform(get("/day"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("\"day\":")))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("\"date\":")))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("\"memorials\":")));
  }

  @Test
  void bladdrarFramOchTillbaka() throws Exception {
    mvc.perform(get("/previous/2021-01-01"))
      .andExpect(status().isOk())
      .andExpect(content().string(org.hamcrest.Matchers.containsString("\"date\": \"2020-12-27\"")));
    mvc.perform(get("/next/2020-11-29"))
      .andExpect(status().isOk())
      .andExpect(content().string(org.hamcrest.Matchers.containsString("\"date\": \"2020-12-06\"")));
  }

  @Test
  void laddarHemIcal() throws Exception {
    mvc.perform(get("/ical/2026"))
      .andExpect(status().isOk())
      .andExpect(header().string("Content-Disposition",
        org.hamcrest.Matchers.containsString("lektionarium_2026.ics")))
      .andExpect(content().string(org.hamcrest.Matchers.startsWith("BEGIN:VCALENDAR")));
  }

  @Test
  @DisplayName("/ical utan årtal ger ett flöde kring dagens datum")
  void prenumerationsflodetTackerNuet() throws Exception {
    String body = mvc.perform(get("/ical"))
      .andExpect(status().isOk())
      .andExpect(header().exists("Last-Modified"))
      .andExpect(content().string(org.hamcrest.Matchers.startsWith("BEGIN:VCALENDAR")))
      .andExpect(content().string(org.hamcrest.Matchers.containsString(
        "REFRESH-INTERVAL;VALUE=DURATION:P1W")))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("SOURCE;VALUE=URI:")))
      .andReturn().getResponse().getContentAsString();

    // Fönstret ska omsluta dagens datum, inte ett godtyckligt år.
    int thisYear = LocalDate.now().getYear();
    assertTrue(body.contains("DTSTART;VALUE=DATE:" + thisYear),
      "flödet ska innehålla dagar från " + thisYear);
    assertTrue(body.contains("DTSTART;VALUE=DATE:" + (thisYear + 1)),
      "flödet ska sträcka sig in i nästa år");
  }

  /** Innehållet ändras först vid nytt kyrkoår, så däremellan ska det bli 304. */
  @Test
  @DisplayName("oförändrat flöde besvaras med 304")
  void prenumerationsflodetSvararMed304() throws Exception {
    String lastModified = mvc.perform(get("/ical"))
      .andReturn().getResponse().getHeader("Last-Modified");

    mvc.perform(get("/ical").header("If-Modified-Since", lastModified))
      .andExpect(status().isNotModified());
  }

  /** Flödet ska inte bara vara ett år, till skillnad från /ical/{år}. */
  @Test
  void prenumerationsflodetArStorreAnEttAr() throws Exception {
    int single = countEvents(mvc.perform(get("/ical/" + LocalDate.now().getYear()))
      .andReturn().getResponse().getContentAsString());
    int feed = countEvents(mvc.perform(get("/ical"))
      .andReturn().getResponse().getContentAsString());
    assertTrue(feed > single, "flödet (" + feed + ") ska ha fler dagar än ett år (" + single + ")");
  }

  private static int countEvents(String ical) {
    int count = 0;
    for (int i = ical.indexOf("BEGIN:VEVENT"); i >= 0; i = ical.indexOf("BEGIN:VEVENT", i + 1)) {
      count++;
    }
    return count;
  }

  @Test
  void laddarHemCsv() throws Exception {
    mvc.perform(get("/csv/2026"))
      .andExpect(status().isOk())
      .andExpect(content().string(org.hamcrest.Matchers.startsWith("Datum;Namn;Tema")));
  }

  @Nested
  @DisplayName("kalendermenyn")
  class Meny {

    @Autowired
    private LiturgicalYearFactory calendar;

    @Autowired
    private Clock clock;

    @Test
    @DisplayName("erbjuder prenumeration på evighetskalendern")
    void lankarTillEvighetskalendern() throws Exception {
      String page = mvc.perform(get("/")).andReturn().getResponse().getContentAsString();
      // webcal-schemat gör länken till en prenumeration i stället för en
      // engångsnedladdning.
      assertTrue(page.contains("href=\"webcal://lektionarium.se/ical\""), page);
      assertTrue(page.contains("Evighetskalender"), page);
    }

    @Test
    @DisplayName("nedladdningarna börjar på innevarande kyrkoår")
    void nedladdningarnaBorjarPaNuvarandeAr() throws Exception {
      int current = calendar.getLiturgicalYear(LocalDate.now(clock)).getYear();
      String page = mvc.perform(get("/")).andReturn().getResponse().getContentAsString();

      assertTrue(page.contains("/ical/" + current), "innevarande kyrkoår ska erbjudas");
      assertFalse(page.contains("/ical/" + (current - 1)),
        "föregående kyrkoår ska inte erbjudas");
      assertTrue(page.contains("/ical/" + (current + 1)), "kommande år ska erbjudas");
    }
  }

  @Nested
  @DisplayName("hälsokontroll")
  class Halsa {

    @Test
    @DisplayName("svarar UP när kalendern kan räkna")
    void svararUp() throws Exception {
      mvc.perform(get("/actuator/health"))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("\"status\":\"UP\"")));
    }

    /** Ändpunkten ska kunna nås utan att röja något om driftsättningen. */
    @Test
    @DisplayName("röjer inga detaljer")
    void rojerIngaDetaljer() throws Exception {
      String body = mvc.perform(get("/actuator/health"))
        .andReturn().getResponse().getContentAsString();
      assertFalse(body.contains("bibleText"), body);
      assertFalse(body.contains("lektionarium.xml"), body);
    }

    /** Bara hälsa exponeras, inte resten av actuator. */
    @Test
    void ovrigaAndpunkterArInteExponerade() throws Exception {
      mvc.perform(get("/actuator/env")).andExpect(status().isNotFound());
      mvc.perform(get("/actuator/beans")).andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("cachning")
  class Cachning {

    /** Ett förflutet kyrkoår ändras aldrig och behöver aldrig hämtas om. */
    @Test
    void arsdataArOforanderlig() throws Exception {
      for (String url : new String[]{"/ical/2026", "/csv/2026", "/txt/2026",
        "/json/2026", "/day/2026-04-05", "/next/2026-04-05", "/previous/2026-04-05"}) {
        mvc.perform(get(url))
          .andExpect(status().isOk())
          .andExpect(header().string("Cache-Control",
            org.hamcrest.Matchers.containsString("immutable")));
      }
    }

    /**
     * Regression: DTSTAMP sattes till anropstidpunkten, så samma år gav olika
     * bytes vid varje hämtning och all villkorad cachning blev meningslös.
     */
    @Test
    @DisplayName("samma år ger byte för byte samma svar")
    void arsdataArDeterministisk() throws Exception {
      assertEquals(body(get("/ical/2026")), body(get("/ical/2026")));
      assertEquals(body(get("/json/2026")), body(get("/json/2026")));
    }

    /** Dagens dag gäller till nästa dag i kyrkoåret, inte en gissad timme. */
    @Test
    void dagensSvarGallerTillNastaKyrkodag() throws Exception {
      String cacheControl = mvc.perform(get("/day"))
        .andExpect(status().isOk())
        .andExpect(header().exists("Last-Modified"))
        .andReturn().getResponse().getHeader("Cache-Control");

      long maxAge = Long.parseLong(cacheControl.replaceAll(".*max-age=(\\d+).*", "$1"));
      assertTrue(maxAge >= 60, "för kort: " + maxAge);
      assertTrue(maxAge <= Duration.ofDays(8).toSeconds(),
        "längre än avståndet mellan två kyrkodagar: " + maxAge);
    }

    @Test
    void dagensSvarKanOmvalideras() throws Exception {
      String lastModified = mvc.perform(get("/day"))
        .andReturn().getResponse().getHeader("Last-Modified");
      mvc.perform(get("/day").header("If-Modified-Since", lastModified))
        .andExpect(status().isNotModified());
    }

    /**
     * Regression: SOURCE byggdes ur Host-huvudet. Ett publikt cachat svar
     * kunde då tala om för alla prenumeranter att hämta om från en adress som
     * den förste anroparen valt.
     */
    @Test
    @DisplayName("Host-huvudet kan inte styra vart klienter hämtar om")
    void hostHuvudetStyrInteFlodet() throws Exception {
      String body = mvc.perform(get("/ical").header("Host", "angripare.example"))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

      assertFalse(body.contains("angripare.example"), body.lines()
        .filter(line -> line.startsWith("SOURCE") || line.startsWith("URL"))
        .toList().toString());
      assertTrue(body.contains("SOURCE;VALUE=URI:https://lektionarium.se/ical"), body);
    }

    /**
     * Varje uträknat kyrkoår tar minne. Utan tak kan anroparen fylla heapen
     * genom att be om år efter år.
     */
    @Test
    @DisplayName("orimliga årtal avvisas")
    void orimligaArtalAvvisas() throws Exception {
      for (String year : new String[]{"2201", "99999", "999999999"}) {
        mvc.perform(get("/ical/" + year))
          .andExpect(status().isBadRequest());
      }
      mvc.perform(get("/ical/2200")).andExpect(status().isOk());
    }

    private String body(org.springframework.test.web.servlet.RequestBuilder request)
      throws Exception {
      return mvc.perform(request).andReturn().getResponse().getContentAsString();
    }
  }

  @Test
  void avvisarOgiltigtDatum() throws Exception {
    mvc.perform(get("/day/inte-ett-datum")).andExpect(status().isBadRequest());
  }

  @Test
  void avvisarArUtanforEvangelieboken() throws Exception {
    mvc.perform(get("/ical/1999")).andExpect(status().isBadRequest());
  }

  @Test
  void framtidaDatumFungerar() throws Exception {
    String date = LocalDate.now().plusYears(1).toString();
    mvc.perform(get("/day/" + date)).andExpect(status().isOk());
  }
}

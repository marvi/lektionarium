package lectio.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * @author marvi
 */
@SpringBootTest(properties = {
  "lektionarium.umami.url=https://statistik.example/script.js",
  "lektionarium.umami.website-id=0f8fad5b-d9cb-469f-a165-70867728950e"
})
class UmamiTest {

  private MockMvc mvc;

  @Autowired
  void setUp(WebApplicationContext context) {
    this.mvc = MockMvcBuilders.webAppContextSetup(context).build();
  }

  @Test
  @DisplayName("spårningstaggen läggs in när en adress är angiven")
  void taggenLaggsIn() throws Exception {
    String page = mvc.perform(get("/")).andReturn().getResponse().getContentAsString();
    assertTrue(page.contains("src=\"https://statistik.example/script.js\""), page);
    assertTrue(page.contains("data-website-id=\"0f8fad5b-d9cb-469f-a165-70867728950e\""), page);
  }

  /** Fragmentet som htmx hämtar är inte en hel sida och ska inte ha skript. */
  @Test
  void fragmentetFarIngenTagg() throws Exception {
    String fragment = mvc.perform(get("/dag/2026-04-05").header("HX-Request", "true"))
      .andReturn().getResponse().getContentAsString();
    assertTrue(!fragment.contains("statistik.example"), fragment);
  }
}

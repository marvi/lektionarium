package lectio.config;

import lectio.cal.LectioRepository;
import lectio.cal.LiturgicalYearFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Clock;
import java.time.ZoneId;

/**
 * @author marvi
 */
@Configuration
public class CalendarConfig implements WebMvcConfigurer {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalendarConfig.class);

  /**
   * En enda kalender för hela applikationen. Den räknar ut varje kyrkoår en
   * gång och är trådsäker, så den kan delas mellan alla anrop.
   * <p>
   * Utan konfiguration används evangelieboken som följer med biblioteket, där
   * bibeltexten är bortstrippad. En driftsättning som har rätt att visa text
   * pekar ut en egen fil med {@code lektionarium.lectionary-file}.
   * <p>
   * Filen läses från filsystemet och aldrig från classpath. En fil som ligger
   * utanför projektet kan inte råka packas in i en jar-fil eller en
   * container-avbild, och därmed inte spridas vidare av misstag.
   */
  @Bean
  public LiturgicalYearFactory liturgicalYearFactory(
    @Value("${lektionarium.lectionary-file:svk_lektionarium.xml}") String lectionaryFile) {

    Path file = Path.of(lectionaryFile);
    if (!Files.isReadable(file)) {
      LOGGER.info("Ingen evangeliebok på {}, använder den medföljande utan bibeltext",
        file.toAbsolutePath());
      return new LiturgicalYearFactory();
    }

    LiturgicalYearFactory factory = new LiturgicalYearFactory(LectioRepository.load(file));
    LOGGER.info("Evangeliebok: {} ({})", file.toAbsolutePath(),
      factory.hasBibleText() ? "med bibeltext" : "utan bibeltext");
    return factory;
  }

  /**
   * Klockan som avgör vilken dag det är.
   * <p>
   * Tidszonen måste anges uttryckligen. Med JVM:ens standardzon skulle en
   * container som kör i UTC byta dag två timmar för sent svensk sommartid, och
   * "dagens dag" i en kyrkoårskalender vore fel under de timmarna.
   */
  @Bean
  public Clock clock(@Value("${lektionarium.zone:Europe/Stockholm}") String zone) {
    return Clock.system(ZoneId.of(zone));
  }

  /** API:et är till för att anropas från andra webbplatser. */
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/api/**").allowedOrigins("*").allowedMethods("GET");
    registry.addMapping("/day").allowedOrigins("*").allowedMethods("GET");
    registry.addMapping("/next/**").allowedOrigins("*").allowedMethods("GET");
    registry.addMapping("/previous/**").allowedOrigins("*").allowedMethods("GET");
    registry.addMapping("/ical/**").allowedOrigins("*").allowedMethods("GET");
  }
}

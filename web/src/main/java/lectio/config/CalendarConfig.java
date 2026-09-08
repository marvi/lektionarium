package lectio.config;

import lectio.cal.LiturgicalYearFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;
import java.time.ZoneId;

/**
 * @author marvi
 */
@Configuration
public class CalendarConfig implements WebMvcConfigurer {

  /**
   * En enda kalender för hela applikationen. Den räknar ut varje kyrkoår en
   * gång och är trådsäker, så den kan delas mellan alla anrop.
   */
  @Bean
  public LiturgicalYearFactory liturgicalYearFactory() {
    return new LiturgicalYearFactory();
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

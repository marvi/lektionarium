package lectio.web;

import lectio.cal.Day;
import lectio.cal.LiturgicalYearFactory;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Kontrollerar att kalendern faktiskt kan svara, inte bara att processen lever.
 * <p>
 * En uträkning av dagens dag rör vid evangelieboken, årscachen och
 * datumlogiken på en gång. Går den igenom fungerar tjänsten; går den inte
 * igenom är det ingen mening att skicka trafik hit.
 *
 * @author marvi
 */
@Component
public class CalendarHealthIndicator implements HealthIndicator {

  private final LiturgicalYearFactory calendar;
  private final BibleTextPolicy textPolicy;
  private final Clock clock;

  public CalendarHealthIndicator(LiturgicalYearFactory calendar, BibleTextPolicy textPolicy,
                                 Clock clock) {
    this.calendar = calendar;
    this.textPolicy = textPolicy;
    this.clock = clock;
  }

  @Override
  public Health health() {
    try {
      Day today = calendar.getCurrentDay(LocalDate.now(clock));
      return Health.up()
        .withDetail("day", today.name())
        .withDetail("date", today.date().toString())
        .withDetail("bibleText", textPolicy.hasBibleText() ? "ja" : "nej")
        .build();
    } catch (RuntimeException ex) {
      return Health.down(ex).build();
    }
  }
}

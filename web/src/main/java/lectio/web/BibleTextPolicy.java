package lectio.web;

import lectio.cal.Day;
import lectio.cal.LiturgicalYearFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Avgör för vilka dagar bibeltexten får visas.
 * <p>
 * Texten i evangelieboken är upphovsrättsskyddad. Rätten att återge den gäller
 * begränsade mängder, inte hela verket, så den visas bara för dagen vi
 * befinner oss i och ett par dagar framåt. Allt annat lämnas ut med enbart
 * bibelhänvisningar.
 * <p>
 * Principen är att stryka som standard: den som vill lämna ut en dag ska gå
 * via {@link #redact}, inte lita på att just den ändpunkten råkar vara
 * ofarlig. Bulkändpunkter som ett helt år stryker alltid, oavsett fönster.
 *
 * @author marvi
 */
@Component
public class BibleTextPolicy {

  private final LiturgicalYearFactory calendar;
  private final int days;

  public BibleTextPolicy(LiturgicalYearFactory calendar,
                         @Value("${lektionarium.text-days:3}") int days) {
    this.calendar = calendar;
    this.days = Math.max(1, days);
  }

  /**
   * @return true om den inlästa evangelieboken alls innehåller bibeltext
   */
  public boolean hasBibleText() {
    return calendar.hasBibleText();
  }

  /**
   * Dagarna som får visa bibeltext: den vi befinner oss i och de närmast
   * följande.
   *
   * @param today dagens datum
   * @return datumen, i kronologisk ordning
   */
  public Set<LocalDate> allowedDates(LocalDate today) {
    Set<LocalDate> allowed = new LinkedHashSet<>();
    LocalDate date = calendar.getCurrentDay(today).date();
    allowed.add(date);
    for (int i = 1; i < days; i++) {
      date = calendar.getNextDay(date).date();
      allowed.add(date);
    }
    return allowed;
  }

  /**
   * @param date  dagen som ska visas
   * @param today dagens datum
   * @return true om bibeltexten får följa med för den dagen
   */
  public boolean mayShowText(LocalDate date, LocalDate today) {
    return hasBibleText() && allowedDates(today).contains(date);
  }

  /**
   * Stryker bibeltexten om dagen ligger utanför fönstret.
   *
   * @param day   dagen att lämna ut
   * @param today dagens datum
   * @return dagen, med eller utan text
   */
  public Day redact(Day day, LocalDate today) {
    return mayShowText(day.date(), today) ? day : day.withoutText();
  }
}

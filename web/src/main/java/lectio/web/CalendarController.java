package lectio.web;

import lectio.cal.Day;
import lectio.cal.LiturgicalYearFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.server.ResponseStatusException;

import java.time.Clock;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.IntStream;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * Sidorna på lektionarium.se.
 * <p>
 * Allt renderas på servern. Länkarna framåt och bakåt fungerar utan JavaScript;
 * med htmx byts bara dagkortet ut i stället för att hela sidan laddas om.
 *
 * @author marvi
 */
@Controller
public class CalendarController {

  private final LiturgicalYearFactory calendar;
  private final BibleTextPolicy textPolicy;
  private final Clock clock;
  private final String feedUrl;

  public CalendarController(LiturgicalYearFactory calendar, BibleTextPolicy textPolicy,
                            Clock clock,
                            @Value("${lektionarium.base-url}") String baseUrl) {
    this.calendar = calendar;
    this.textPolicy = textPolicy;
    this.clock = clock;
    this.feedUrl = (baseUrl.endsWith("/")
      ? baseUrl.substring(0, baseUrl.length() - 1)
      : baseUrl) + "/ical";
  }

  @GetMapping("/")
  public String today(Model model,
                      @RequestHeader(value = "HX-Request", required = false) String htmx) {
    return render(model, LocalDate.now(clock), htmx);
  }

  @GetMapping("/dag/{date}")
  public String day(@PathVariable String date, Model model,
                    @RequestHeader(value = "HX-Request", required = false) String htmx) {
    return render(model, parseDate(date), htmx);
  }

  private String render(Model model, LocalDate date, String htmx) {
    // Bibeltexten följer med bara för dagen vi befinner oss i och de närmast
    // följande. Övriga dagar visas med enbart bibelhänvisningar.
    Day day = textPolicy.redact(calendar.getCurrentDay(date), LocalDate.now(clock));
    DayView view = DayView.of(day,
      calendar.getPreviousDay(day.date()).date(),
      calendar.getNextDay(day.date()).date());

    model.addAttribute("day", view);
    // Ett htmx-anrop vill bara ha kortet, en vanlig webbläsare hela sidan.
    if (htmx != null) {
      return "dagkort";
    }
    model.addAttribute("years", downloadableYears());
    model.addAttribute("feedUrl", feedUrl);
    model.addAttribute("feedWebcal", webcal(feedUrl));
    return "index";
  }

  /** Åren som erbjuds för nedladdning: innevarande kyrkoår och några framåt. */
  private List<Integer> downloadableYears() {
    int current = calendar.getLiturgicalYear(LocalDate.now(clock)).getYear();
    return IntStream.rangeClosed(current, current + 2).boxed().toList();
  }

  /**
   * Samma adress med webcal-schemat.
   * <p>
   * En vanlig https-länk till en .ics-fil laddar bara hem den en gång.
   * webcal-schemat är det kalenderklienterna har registrerat sig för, så ett
   * klick blir en prenumeration som håller sig uppdaterad.
   */
  private static String webcal(String url) {
    return url.replaceFirst("^https?://", "webcal://");
  }

  private static LocalDate parseDate(String date) {
    try {
      return LocalDate.parse(date);
    } catch (DateTimeParseException ex) {
      throw new ResponseStatusException(BAD_REQUEST, "Ogiltigt datum: " + date, ex);
    }
  }
}

package lectio.api;

import lectio.cal.Day;
import lectio.cal.LiturgicalYear;
import lectio.cal.LiturgicalYearFactory;
import lectio.format.CalendarBasis;
import lectio.format.CsvFormat;
import lectio.format.IcalFormat;
import lectio.format.JsonFormat;
import lectio.format.TextFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

/**
 * HTTP-API för kalenderdata.
 * <p>
 * Svaren bygger på {@link JsonFormat} och de andra formatklasserna i
 * biblioteket, så webbtjänsten och kommandoraden ger exakt samma innehåll.
 *
 * <h2>Cachning</h2>
 * Ändpunkterna delas i två slag, och de cachas olika:
 * <ul>
 *   <li>De som får ett datum eller ett årtal i adressen är rena funktioner av
 *       den adressen. Kyrkoåret 2026 är detsamma i dag som om tio år, så de
 *       svarar {@code immutable} med lång livslängd.</li>
 *   <li>De som utgår från dagens datum ändras vid en känd tidpunkt. De får en
 *       livslängd som räknas fram till just den tidpunkten, inte en gissad
 *       siffra, och bär {@code Last-Modified} så att omhämtning kan besvaras
 *       med 304.</li>
 * </ul>
 * Formatklasserna ger samma bytes för samma innehåll, vilket är det som gör
 * villkorade hämtningar meningsfulla.
 *
 * @author marvi
 */
@RestController
public class CalendarApiController {

  private static final MediaType TEXT_CALENDAR = MediaType.parseMediaType("text/calendar");
  private static final MediaType TEXT_CSV = MediaType.parseMediaType("text/csv");
  private static final MediaType TEXT_PLAIN_UTF8 =
    new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8);

  /**
   * Övre gräns för vilka år som besvaras.
   * <p>
   * Utan tak kan vem som helst be om år efter år och tvinga fram uträkningar
   * som varken evangelieboken eller kyrkoordningen säger något om.
   */
  private static final int LAST_SUPPORTED_YEAR = 2200;

  /** Data som inte kan ändras behöver aldrig hämtas om. */
  private static final CacheControl IMMUTABLE =
    CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic().immutable();

  /** Flödet ändras bara vid nytt kyrkoår, men ska ändå kunna rättas i drift. */
  private static final CacheControl FEED =
    CacheControl.maxAge(6, TimeUnit.HOURS).cachePublic();

  private final LiturgicalYearFactory calendar;
  private final Clock clock;
  private final String baseUrl;

  public CalendarApiController(LiturgicalYearFactory calendar, Clock clock,
                               @Value("${lektionarium.base-url}") String baseUrl) {
    this.calendar = calendar;
    this.clock = clock;
    this.baseUrl = baseUrl.endsWith("/")
      ? baseUrl.substring(0, baseUrl.length() - 1)
      : baseUrl;
  }

  /**
   * Dagen i kyrkoåret just nu.
   * <p>
   * Svaret gäller fram till nästa dag i kyrkoåret infaller, vilket kan vara
   * flera dagar bort. Livslängden räknas fram till den tidpunkten.
   */
  @GetMapping(value = "/day", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> today(WebRequest request) {
    Day day = calendar.getCurrentDay(today());
    Instant validFrom = startOfDay(day.date());
    if (request.checkNotModified(validFrom.toEpochMilli())) {
      return null;
    }
    Instant validUntil = startOfDay(calendar.getNextDay(day.date()).date());
    return ResponseEntity.ok()
      .cacheControl(CacheControl.maxAge(secondsUntil(validUntil), TimeUnit.SECONDS).cachePublic())
      .lastModified(validFrom)
      .contentType(MediaType.APPLICATION_JSON)
      .body(JsonFormat.forDay(day));
  }

  /** Dagen i kyrkoåret för ett visst datum. */
  @GetMapping(value = "/day/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> day(@PathVariable String date) {
    return immutableJson(JsonFormat.forDay(calendar.getCurrentDay(parseDate(date))));
  }

  @GetMapping(value = "/next/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> next(@PathVariable String date) {
    return immutableJson(JsonFormat.forDay(calendar.getNextDay(parseDate(date))));
  }

  @GetMapping(value = "/previous/{date}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> previous(@PathVariable String date) {
    return immutableJson(JsonFormat.forDay(calendar.getPreviousDay(parseDate(date))));
  }

  /**
   * Hela kyrkoåret som ett flöde att prenumerera på, utan årtal i adressen.
   * <p>
   * Adressen är beständig och innehållet följer med tiden: varje anrop räknar
   * ut ett fönster kring dagens datum. Fönstret är framtungt, eftersom
   * kalenderklienter hämtar om när det passar dem och en klient som legat
   * stilla ett halvår ändå ska ha kommande dagar.
   */
  @GetMapping(value = "/ical", produces = "text/calendar")
  public ResponseEntity<String> subscription(WebRequest request) {
    int current = calendar.getLiturgicalYear(today()).getYear();
    int firstYear = Math.max(LiturgicalYear.FIRST_SUPPORTED_YEAR, current - 1);
    int lastYear = current + 1;

    // Fönstret byter innehåll när kyrkoåret gör det, alltså första advent.
    Instant lastModified = startOfDay(calendar.startOfLiturgicalYear(current));
    if (request.checkNotModified(lastModified.toEpochMilli())) {
      return null;
    }

    // Adressen kommer ur konfigurationen, inte ur Host-huvudet. Ett svar som
    // får cachas publikt och som talar om var klienten ska hämta om måste inte
    // kunna styras av den som råkar skicka det första anropet.
    String body = IcalFormat.forSubscription(
      calendar.getDaysOfLiturgicalYears(firstYear, lastYear).values(),
      "Kyrkoåret", baseUrl + "/ical", lastModified);

    return ResponseEntity.ok()
      .cacheControl(FEED)
      .lastModified(lastModified)
      .contentType(TEXT_CALENDAR)
      .body(body);
  }

  /** Hela året som JSON. */
  @GetMapping(value = "/json/{year}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> jsonYear(@PathVariable int year,
                                         @RequestParam(defaultValue = "LITURGICAL") CalendarBasis basis) {
    return immutableJson(JsonFormat.forYear(calendar, basis, checkYear(year)));
  }

  /** Ett enskilt år som iCalendar, att ladda hem. */
  @GetMapping(value = "/ical/{year}", produces = "text/calendar")
  public ResponseEntity<String> ical(@PathVariable int year,
                                     @RequestParam(defaultValue = "LITURGICAL") CalendarBasis basis) {
    return download(IcalFormat.forYear(calendar, basis, checkYear(year)),
      TEXT_CALENDAR, "lektionarium_" + year + ".ics");
  }

  @GetMapping(value = "/csv/{year}", produces = "text/csv")
  public ResponseEntity<String> csv(@PathVariable int year,
                                    @RequestParam(defaultValue = "LITURGICAL") CalendarBasis basis) {
    return download(CsvFormat.forYear(calendar, basis, checkYear(year)),
      TEXT_CSV, "lektionarium_" + year + ".csv");
  }

  @GetMapping(value = "/txt/{year}", produces = MediaType.TEXT_PLAIN_VALUE)
  public ResponseEntity<String> text(@PathVariable int year,
                                     @RequestParam(defaultValue = "LITURGICAL") CalendarBasis basis) {
    return ResponseEntity.ok()
      .cacheControl(IMMUTABLE)
      .contentType(TEXT_PLAIN_UTF8)
      .body(TextFormat.forYear(calendar, basis, checkYear(year)));
  }

  private LocalDate today() {
    return LocalDate.now(clock);
  }

  private Instant startOfDay(LocalDate date) {
    return date.atStartOfDay(clock.getZone()).toInstant();
  }

  private long secondsUntil(Instant instant) {
    // Aldrig noll: ett svar utan livslängd tvingar fram omhämtning vid varje
    // sidvisning även när innehållet står stilla.
    return Math.max(60, Duration.between(clock.instant(), instant).toSeconds());
  }

  private static ResponseEntity<String> immutableJson(String body) {
    return ResponseEntity.ok()
      .cacheControl(IMMUTABLE)
      .contentType(MediaType.APPLICATION_JSON)
      .body(body);
  }

  private static ResponseEntity<String> download(String body, MediaType type, String filename) {
    return ResponseEntity.ok()
      .cacheControl(IMMUTABLE)
      .contentType(type)
      .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
      .body(body);
  }

  private static LocalDate parseDate(String date) {
    try {
      return LocalDate.parse(date);
    } catch (DateTimeParseException ex) {
      throw new ResponseStatusException(BAD_REQUEST, "Ogiltigt datum: " + date, ex);
    }
  }

  private static int checkYear(int year) {
    if (year < LiturgicalYear.FIRST_SUPPORTED_YEAR || year > LAST_SUPPORTED_YEAR) {
      throw new ResponseStatusException(BAD_REQUEST, "Endast år "
        + LiturgicalYear.FIRST_SUPPORTED_YEAR + "-" + LAST_SUPPORTED_YEAR + " stöds");
    }
    return year;
  }
}

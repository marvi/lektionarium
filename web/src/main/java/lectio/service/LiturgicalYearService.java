package lectio.service;

import lectio.cal.Day;
// HolyDay import might not be needed here anymore if service only deals with Day
// import lectio.cal.HolyDay;
import lectio.cal.LiturgicalYearFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
// DateTimeFormatter might not be needed here if Day object is returned directly
// import java.time.format.DateTimeFormatter;
// HashMap and Map might not be needed here
// import java.util.HashMap;
// import java.util.Map;

@Service
public class LiturgicalYearService {

  private static final LiturgicalYearFactory lyf = new LiturgicalYearFactory();
  // private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // No longer formatting date here

  public Day currentDay(LocalDate d) {
    return lyf.getCurrentDay(d);
  }

  public Day nextDay(LocalDate d) {
    return lyf.getNextDay(d);
  }

  public Day previousDay(LocalDate d) {
    return lyf.getPreviousDay(d);
  }

  // generateDay method is no longer needed as the service returns Day objects directly.
  // The controller will handle passing the Day object to the model,
  // and Spring/Jackson will handle JSON serialization from the Day record.
}

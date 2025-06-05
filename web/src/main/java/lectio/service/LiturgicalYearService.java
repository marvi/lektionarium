package lectio.service;

import lectio.cal.Day;
import lectio.cal.HolyDay;
import lectio.cal.LiturgicalYearFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class LiturgicalYearService {

  // Making lyf non-static as per typical Spring service design, unless it's truly global and stateless.
  // If it must be static final, it should be initialized in a static block or directly.
  private static final LiturgicalYearFactory lyf = new LiturgicalYearFactory(); // Added private
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd"); // Added final and private

  public Map<String, Object> currentDay(LocalDate d) { // Added public and specified Map generics
    return generateDay(lyf.getCurrentDay(d));
  }

  public Map<String, Object> nextDay(LocalDate d) { // Added public and specified Map generics
    return generateDay(lyf.getNextDay(d));
  }

  public Map<String, Object> previousDay(LocalDate d) { // Added public and specified Map generics
    return generateDay(lyf.getPreviousDay(d));
  }

  // Changed to public, specified Map generics
  public Map<String, Object> generateDay(Day day) {
    Map<String, Object> dayMap = new HashMap<>();
    dayMap.put("day", day.getName()); // Changed from day.name to day.getName()
    dayMap.put("date", formatter.format(day.getDate())); // Changed from day.date to day.getDate()
    dayMap.put("memorials", day.getMemorials()); // Changed from day.memorials to day.getMemorials()

    if (day instanceof HolyDay) {
      dayMap.put("readings", ((HolyDay) day).getReadings()); // Changed from .readings to .getReadings()
    }
    return dayMap;
  }
}

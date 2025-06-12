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

  public Map<String, Object> generateDay(lectio.cal.LiturgicalDay day) {
    Map<String, Object> dayMap = new HashMap<>();
    dayMap.put("day", day.name());
    dayMap.put("date", formatter.format(day.date()));
    dayMap.put("memorials", day.memorials());

    if (day instanceof HolyDay) {
      dayMap.put("readings", ((HolyDay) day).readings());
    }
    return dayMap;
  }
}

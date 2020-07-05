package lectio.service

import lectio.cal.Day
import lectio.cal.HolyDay
import lectio.cal.LiturgicalYearFactory
import org.springframework.stereotype.Service

import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class LiturgicalYearService {

  static final LiturgicalYearFactory lyf = new LiturgicalYearFactory()
  DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  Map currentDay(LocalDate d) {
    return generateDay(lyf.getCurrentDay(d))
  }

  Map nextDay(LocalDate d) {
    return generateDay(lyf.getNextDay(d))
  }

  Map previousDay(LocalDate d) {
    return generateDay(lyf.getPreviousDay(d))
  }

  Map generateDay(Day day) {
    if(day instanceof HolyDay) {
      return [day: day.name, date: formatter.format(day.date), memorials: day.memorials, readings: ((HolyDay) day).readings]
    }
    else {
      return [day: day.name, date: formatter.format(day.date), memorials: day.memorials]
    }
  }

}

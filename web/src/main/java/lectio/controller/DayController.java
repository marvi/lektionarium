package lectio.controller;

import lectio.service.LiturgicalYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class DayController {

  @Autowired
  private LiturgicalYearService liturgicalYearService;

  @RequestMapping(value = "/day", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  @CrossOrigin
  public Map<String, Object> day() {
    return liturgicalYearService.currentDay(LocalDate.now());
  }

  @RequestMapping(value = "/next/{date}", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  @CrossOrigin
  public Map<String, Object> next(@PathVariable("date") String date) {
    Map<String, Object> day = liturgicalYearService.nextDay(LocalDate.parse(date));
    return day;
  }

  @RequestMapping(value = "/previous/{date}", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  @CrossOrigin
  public Map<String, Object> previous(@PathVariable("date") String date) {
    return liturgicalYearService.previousDay(LocalDate.parse(date));
  }
}

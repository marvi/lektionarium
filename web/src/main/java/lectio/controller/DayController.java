package lectio.controller;

import lectio.cal.Reading;
import lectio.cal.Readings;
import lectio.service.LiturgicalYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class DayController {

  private static final Logger log = Logger.getLogger(DayController.class.getName());

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
  public Map<String, Object> next(@PathVariable String date) {
    Map<String, Object> day = liturgicalYearService.nextDay(LocalDate.parse(date));
    return day;
  }

  @RequestMapping(value = "/previous/{date}", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  @CrossOrigin
  public Map<String, Object> previous(@PathVariable String date) {
    return liturgicalYearService.previousDay(LocalDate.parse(date));
  }

  // Web view endpoints

  @GetMapping("/web-day")
  public String webDay(Model model) {
    log.info("Web day view requested");
    Map<String, Object> dayData = liturgicalYearService.currentDay(LocalDate.now());
    processReadingsForTemplate(dayData);
    model.addAttribute("dayData", dayData);
    return "day";
  }

  @GetMapping("/web-next/{date}")
  public String webNext(Model model, @PathVariable("date") String date) {
    log.info("Web next day view requested for date: " + date);
    Map<String, Object> dayData = liturgicalYearService.nextDay(LocalDate.parse(date));
    processReadingsForTemplate(dayData);
    model.addAttribute("dayData", dayData);
    return "day";
  }

  @GetMapping("/web-previous/{date}")
  public String webPrevious(Model model, @PathVariable("date") String date) {
    log.info("Web previous day view requested for date: " + date);
    Map<String, Object> dayData = liturgicalYearService.previousDay(LocalDate.parse(date));
    processReadingsForTemplate(dayData);
    model.addAttribute("dayData", dayData);
    return "day";
  }

  /**
   * Processes the readings in the dayData map to make them compatible with the template.
   * Converts a Readings object to a List of Maps, where each Map represents a reading.
   *
   * @param dayData the map containing the day data
   */
  private void processReadingsForTemplate(Map<String, Object> dayData) {
    if (dayData.containsKey("readings") && dayData.get("readings") instanceof Readings) {
      Readings readings = (Readings) dayData.get("readings");
      List<Map<String, String>> readingsList = new ArrayList<>();

      // Process Old Testament reading
      if (readings.getOt() != null) {
        Map<String, String> otMap = new HashMap<>();
        otMap.put("type", "Gamla testamentet");
        otMap.put("text", readings.getOt().getSweRef());
        if (readings.getOt().getText() != null) {
          otMap.put("content", readings.getOt().getText());
        }
        readingsList.add(otMap);
      }

      // Process Epistle reading
      if (readings.getEp() != null) {
        Map<String, String> epMap = new HashMap<>();
        epMap.put("type", "Epistel");
        epMap.put("text", readings.getEp().getSweRef());
        if (readings.getEp().getText() != null) {
          epMap.put("content", readings.getEp().getText());
        }
        readingsList.add(epMap);
      }

      // Process Gospel reading
      if (readings.getGo() != null) {
        Map<String, String> goMap = new HashMap<>();
        goMap.put("type", "Evangelium");
        goMap.put("text", readings.getGo().getSweRef());
        if (readings.getGo().getText() != null) {
          goMap.put("content", readings.getGo().getText());
        }
        readingsList.add(goMap);
      }

      // Process Psalm reading
      if (readings.getPs() != null) {
        Map<String, String> psMap = new HashMap<>();
        psMap.put("type", "Psaltaren");
        psMap.put("text", readings.getPs().getSweRef());
        if (readings.getPs().getText() != null) {
          psMap.put("content", readings.getPs().getText());
        }
        readingsList.add(psMap);
      }

      // Process Alternative reading
      if (readings.getAlt() != null) {
        Map<String, String> altMap = new HashMap<>();
        altMap.put("type", "Alternativ text");
        altMap.put("text", readings.getAlt().getSweRef());
        if (readings.getAlt().getText() != null) {
          altMap.put("content", readings.getAlt().getText());
        }
        readingsList.add(altMap);
      }

      // Add theme if available
      if (readings.getTheme() != null) {
        Map<String, String> themeMap = new HashMap<>();
        themeMap.put("type", "Tema");
        themeMap.put("text", readings.getTheme());
        readingsList.add(themeMap);
      }

      // Replace the Readings object with the List of Maps
      dayData.put("readings", readingsList);
    }
  }
}

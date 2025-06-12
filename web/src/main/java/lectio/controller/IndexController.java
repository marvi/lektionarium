package lectio.controller;

import gg.jte.ContentType;
import jakarta.servlet.http.HttpServletResponse;
import lectio.cal.Readings;
import lectio.service.LiturgicalYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class IndexController {

  Logger log = Logger.getLogger(IndexController.class.getName());

  @Autowired
  private LiturgicalYearService liturgicalYearService;

  @GetMapping("/")
  public String view(Model model, HttpServletResponse response) {
    log.info("Index mapping called " + ContentType.Html);

    // Get current year and next four years
    int currentYear = Year.now().getValue();
    List<Integer> years = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      years.add(currentYear + i);
    }
    model.addAttribute("years", years);

    // Get today's readings
    Map<String, Object> dayData = liturgicalYearService.currentDay(LocalDate.now());
    processReadingsForTemplate(dayData);
    model.addAttribute("dayData", dayData);

    return "index";
  }

  @GetMapping("/day-readings")
  public String dayReadings(Model model) {
    Map<String, Object> dayData = liturgicalYearService.currentDay(LocalDate.now());
    processReadingsForTemplate(dayData);
    model.addAttribute("dayData", dayData);
    return "day-readings";
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

      // Add theme directly to dayData if available
      if (readings.getTheme() != null) {
        dayData.put("theme", readings.getTheme());
      }

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

      // Replace the Readings object with the List of Maps
      dayData.put("readings", readingsList);
    }
  }
}

package lectio.controller;

import lectio.cal.Day; // Added import
import lectio.service.LiturgicalYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType; // Added import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
// No longer returning Map from service, so Map import might not be needed directly here
// import java.util.Map;

@Controller
public class DayController {

  @Autowired
  private LiturgicalYearService liturgicalYearService;

  @GetMapping("/")
  public String index(Model model) {
    // The actual day data will be loaded by HTMX via /day
    // So, this initial page doesn't strictly need to pass the day data
    // However, we can pass it if we want to render the initial view without waiting for htmx
    // For now, let's keep it simple and let htmx load it.
    return "index"; // Renders index.html
  }

  // JSON endpoint for current day (default)
  @GetMapping("/day") // Removed 'produces' to make it default
  @ResponseBody
  @CrossOrigin
  public Day getDayJson() {
    return liturgicalYearService.currentDay(LocalDate.now());
  }

  // HTML endpoint for current day
  @GetMapping(value = "/day", produces = MediaType.TEXT_HTML_VALUE) // Explicitly produces HTML
  public String getDayHtml(Model model) {
    Day dayObject = liturgicalYearService.currentDay(LocalDate.now());
    model.addAttribute("day", dayObject);
    return "day-card :: dayCard";
  }

  // JSON endpoint for next day
  @GetMapping(value = "/next/{date}", produces="application/json")
  @ResponseBody
  @CrossOrigin
  public Day next(@PathVariable("date") String date) { // Return Day object
    Day dayObject = liturgicalYearService.nextDay(LocalDate.parse(date));
    return dayObject;
  }

  // HTML endpoint for next day
  @GetMapping(value = "/next/{date}", produces = "text/html")
  public String next(@PathVariable("date") String date, Model model) {
    Day dayObject = liturgicalYearService.nextDay(LocalDate.parse(date)); // Get Day object
    model.addAttribute("day", dayObject); // Add Day object directly
    return "day-card :: dayCard";
  }

  // JSON endpoint for previous day
  @GetMapping(value = "/previous/{date}", produces="application/json")
  @ResponseBody
  @CrossOrigin
  public Day previous(@PathVariable("date") String date) { // Return Day object
    return liturgicalYearService.previousDay(LocalDate.parse(date));
  }

  // HTML endpoint for previous day
  @GetMapping(value = "/previous/{date}", produces = "text/html")
  public String previous(@PathVariable("date") String date, Model model) {
    Day dayObject = liturgicalYearService.previousDay(LocalDate.parse(date)); // Get Day object
    model.addAttribute("day", dayObject); // Add Day object directly
    return "day-card :: dayCard";
  }
}

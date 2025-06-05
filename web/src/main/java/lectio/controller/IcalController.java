package lectio.controller;

import lectio.service.LiturgicalYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

// Assuming getIcalForYear is a static method in IcalFormat, so static import:
import static lectio.format.IcalFormat.getIcalForYear;

@Controller
public class IcalController {

  @Autowired
  private LiturgicalYearService liturgicalYearService; // Added private modifier

  @RequestMapping(value = "/ical/{year}", method = RequestMethod.GET, produces="text/calendar")
  @ResponseBody
  @CrossOrigin
  public HttpEntity<byte[]> ical(@PathVariable String year) {
    System.out.println("Downloading calendar for " + year); // Changed from println
    int forYear = Integer.parseInt(year);

    String calData = getIcalForYear(forYear);
    HttpHeaders header = new HttpHeaders();
    header.set("Content-Type", "text/calendar");
    header.set(HttpHeaders.CONTENT_DISPOSITION,
      "attachment; filename=lektionarium_" + year + ".ics");
    // setContentLength is often managed by Spring automatically based on HttpEntity<byte[]>
    // header.setContentLength(calData.getBytes().length); // Consider removing if Spring handles it

    return new HttpEntity<>(calData.getBytes(), header); // Added diamond operator
  }
}

package lectio.controller

import lectio.format.IcalFormat
import lectio.service.LiturgicalYearService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import static lectio.format.IcalFormat.*

@Controller
class IcalController {

  @Autowired
  LiturgicalYearService liturgicalYearService

  @RequestMapping(value = "/ical/{year}", method = RequestMethod.GET, produces="text/calendar")
  @ResponseBody
  @CrossOrigin
  HttpEntity<byte[]> ical(@PathVariable String year) {
    println "Downloading calendar for ${year}"
    int forYear = Integer.parseInt(year)

    String calData = getIcalForYear(forYear)
    HttpHeaders header = new HttpHeaders()
    header.set("Content-Type", "text/calendar")
    header.set(HttpHeaders.CONTENT_DISPOSITION,
      "attachment; filename=lektionarium_" + year + ".ics")
    header.setContentLength(calData.getBytes().length)

    return new HttpEntity<byte[]>(calData.getBytes(), header)
  }


}

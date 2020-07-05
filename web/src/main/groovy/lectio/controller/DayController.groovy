package lectio.controller


import lectio.service.LiturgicalYearService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

import java.time.LocalDate

@Controller
class DayController {

  @Autowired
  LiturgicalYearService liturgicalYearService

  @RequestMapping(value = "/day", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  @CrossOrigin
  Map day() {
    return liturgicalYearService.currentDay(LocalDate.now())
  }

  @RequestMapping(value = "/next/{date}", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  @CrossOrigin
  Map next(@PathVariable String date) {
    def day = liturgicalYearService.nextDay(LocalDate.parse(date))
    return day
  }

  @RequestMapping(value = "/previous/{date}", method = RequestMethod.GET, produces="application/json")
  @ResponseBody
  @CrossOrigin
  Map previous(@PathVariable String date) {
    return liturgicalYearService.previousDay(LocalDate.parse(date))
  }



}

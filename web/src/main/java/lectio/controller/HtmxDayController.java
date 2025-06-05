package lectio.controller;

import lectio.service.LiturgicalYearService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDate;
import java.util.Map;

@Controller
public class HtmxDayController {

    @Autowired
    private LiturgicalYearService liturgicalYearService;

    @GetMapping("/")
    public String index(Model model) {
        // The actual day data will be loaded by HTMX via /day-htmx
        // So, this initial page doesn't strictly need to pass the day data
        // However, we can pass it if we want to render the initial view without waiting for htmx
        // For now, let's keep it simple and let htmx load it.
        return "index"; // Renders index.html
    }

    @GetMapping("/day-htmx") // Renamed to avoid conflict with existing /day JSON endpoint
    public String getCurrentDay(Model model) {
        Map<String, Object> dayData = liturgicalYearService.currentDay(LocalDate.now());
        model.addAttribute("day", dayData);
        return "day-card :: dayCard"; // Renders the dayCard fragment from day-card.html
    }

    @GetMapping("/next-htmx/{date}") // Renamed to avoid conflict
    public String getNextDay(@PathVariable String date, Model model) {
        Map<String, Object> dayData = liturgicalYearService.nextDay(LocalDate.parse(date));
        model.addAttribute("day", dayData);
        return "day-card :: dayCard"; // Renders the dayCard fragment
    }

    @GetMapping("/previous-htmx/{date}") // Renamed to avoid conflict
    public String getPreviousDay(@PathVariable String date, Model model) {
        Map<String, Object> dayData = liturgicalYearService.previousDay(LocalDate.parse(date));
        model.addAttribute("day", dayData);
        return "day-card :: dayCard"; // Renders the dayCard fragment
    }
}

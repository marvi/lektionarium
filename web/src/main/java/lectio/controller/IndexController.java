package lectio.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

  @GetMapping("/")
  public String view(Model model, HttpServletResponse response) {
    model.addAttribute("model", "demo");
    return "index";
  }
}

package lectio.controller;

import gg.jte.ContentType;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.logging.Logger;

@Controller
public class IndexController {

  Logger log = Logger.getLogger(IndexController.class.getName());

  @GetMapping("/")
  public String view(Model model, HttpServletResponse response) {
    log.info("Index mapping called " + ContentType.Html);
    model.addAttribute("hello", "demo");
    return "index";
  }
}

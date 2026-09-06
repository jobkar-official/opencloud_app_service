
package dev.opencloud.presentation;

import dev.opencloud.domain.entity.Server;
import dev.opencloud.domain.repository.ServerRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/servers")
public class ServerWebController {
  private final ServerRepository repo;

  public ServerWebController(ServerRepository r) {
    repo = r;
  }

  @GetMapping
  public String list(Model m) {
    m.addAttribute("servers", repo.findAll());
    return "servers/list";
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable String id, Model m) {
    m.addAttribute("server", repo.findById(id).orElseThrow());
    return "servers/detail";
  }

  @PostMapping
  public String create(@ModelAttribute Server s) {
    repo.save(s);
    return "redirect:/servers";
  }
}

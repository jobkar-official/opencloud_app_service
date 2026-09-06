
package dev.opencloud.presentation.web;
import dev.opencloud.domain.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class DashboardController {
  private final ServerRepository serverRepo;
  private final DeploymentRepository deploymentRepo;
  public DashboardController(ServerRepository s, DeploymentRepository d){serverRepo=s;deploymentRepo=d;}
  @GetMapping("/dashboard")
  public String dashboard(Model m){
    m.addAttribute("servers", serverRepo.findAll());
    m.addAttribute("deployments", deploymentRepo.findAll());
    m.addAttribute("totalServers", serverRepo.count());
    m.addAttribute("liveDeployments", deploymentRepo.findAll().stream().filter(d->d.getStatus().name().equals("LIVE")).count());
    return "dashboard/overview";
  }
  @GetMapping("/") public String root(){return "redirect:/dashboard";}
}

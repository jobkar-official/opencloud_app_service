package dev.opencloud.presentation.web;

import dev.opencloud.domain.entity.*;
import dev.opencloud.domain.repository.*;
import dev.opencloud.application.service.DeployOrchestratorService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/deployments")
public class DeploymentWebController {
  private final DeploymentRepository deploymentRepo;
  private final ServerRepository serverRepo;
  private final DeployOrchestratorService orchestrator;
  private final OAuth2AuthorizedClientService authorizedClientService;

  public DeploymentWebController(DeploymentRepository d, ServerRepository s,
      DeployOrchestratorService o, OAuth2AuthorizedClientService clientService) {
    deploymentRepo = d;
    serverRepo = s;
    orchestrator = o;
    this.authorizedClientService = clientService;
  }

  @GetMapping
  public String list(Model m) {
    m.addAttribute("deployments", deploymentRepo.findAll());
    return "deployments/list";
  }

  @GetMapping("/new")
  public String createForm(Model m,
      @AuthenticationPrincipal OAuth2User oauthUser,
      Authentication authentication) {

    m.addAttribute("servers", serverRepo.findAll());

    // Safe GitHub fetch - no 500 even if not connected
    try {z
      if (authentication instanceof OAuth2AuthenticationToken oauthToken
          && "github".equals(oauthToken.getAuthorizedClientRegistrationId())) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
            oauthToken.getAuthorizedClientRegistrationId(),
            oauthToken.getName());

        if (client != null && oauthUser != null) {
          String login = oauthUser.getAttribute("login");
          m.addAttribute("isConnected", true);
          m.addAttribute("isGitHubConnected", true);
          m.addAttribute("connectedUser", login != null ? login : oauthUser.getName());
          m.addAttribute("providerInput", "GITHUB");

          String token = client.getAccessToken().getTokenValue();
          RestTemplate rest = new RestTemplate();
          HttpHeaders headers = new HttpHeaders();
          headers.setBearerAuth(token);
          headers.set("Accept", "application/vnd.github.v3+json");
          headers.set("User-Agent", "OpenCloud-Dashboard");
          HttpEntity<String> entity = new HttpEntity<>(headers);

          ResponseEntity<List> response = rest.exchange(
              "https://api.github.com/user/repos?per_page=100&sort=updated&affiliation=owner,collaborator,organization_member",
              HttpMethod.GET, entity, List.class);

          List<Map> repos = response.getBody();
          m.addAttribute("githubRepos", repos);
          System.out.println("GitHub repos fetched: " + (repos != null ? repos.size() : 0));
        }
      }
    } catch (Exception e) {
      System.out.println("GitHub fetch failed, showing manual input: " + e.getMessage());
      // Don't crash - just show manual URL input
    }

    return "deployments/new";
  }

  @PostMapping("/new")
  public String create(@RequestParam String repoUrl, @RequestParam String provider,
      @RequestParam String serverId, @RequestParam String buildType,
      @RequestParam(required = false) String envVars) {
    Deployment d = new Deployment();
    d.setName(repoUrl.substring(repoUrl.lastIndexOf('/') + 1).replace(".git", ""));
    d.setRepoUrl(repoUrl);
    d.setProvider(Deployment.RepoProvider.valueOf(provider));
    d.setBuildType(Deployment.BuildType.valueOf(buildType));
    d.setServer(serverRepo.findById(serverId).orElse(null));
    if (envVars != null) {
      Arrays.stream(envVars.split("\n")).filter(l -> l.contains("=")).forEach(line -> {
        String[] kv = line.split("=", 2);
        d.getEnvVars().add(new EnvVar(kv[0].trim(), kv[1].trim(), kv[0].toLowerCase().contains("secret")));
      });
    }
    deploymentRepo.save(d);
    orchestrator.triggerDeploy(d.getId(), "HEAD");
    return "redirect:/deployments/" + d.getId();
  }

  @GetMapping("/{id}")
  public String detail(@PathVariable String id, Model m) {
    m.addAttribute("deployment", deploymentRepo.findById(id).orElseThrow());
    return "deployments/detail";
  }

  @PostMapping("/{id}/redeploy")
  public String redeploy(@PathVariable String id) {
    orchestrator.triggerDeploy(id, null);
    return "redirect:/deployments/" + id;
  }
}
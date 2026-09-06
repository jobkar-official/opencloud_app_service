package dev.opencloud.presentation.web;

import dev.opencloud.domain.entity.Deployment;
import dev.opencloud.domain.entity.EnvVar;
import dev.opencloud.domain.repository.DeploymentRepository;
import dev.opencloud.domain.repository.ServerRepository;
import dev.opencloud.application.service.DeployOrchestratorService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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

  public DeploymentWebController(
      DeploymentRepository deploymentRepo,
      ServerRepository serverRepo,
      DeployOrchestratorService orchestrator,
      OAuth2AuthorizedClientService authorizedClientService) {

    this.deploymentRepo = deploymentRepo;
    this.serverRepo = serverRepo;
    this.orchestrator = orchestrator;
    this.authorizedClientService = authorizedClientService;
  }

  /**
   * Deployment page.
   *
   * This is the page where the user:
   * 1. Connects GitHub
   * 2. Selects repository
   * 3. Selects server
   * 4. Selects build type
   * 5. Deploys
   */
  @GetMapping
  public String list(
      Model model,
      @AuthenticationPrincipal OAuth2User oauthUser,
      Authentication authentication) {

    model.addAttribute("deployments", deploymentRepo.findAll());
    model.addAttribute("servers", serverRepo.findAll());

    System.out.println("========== /deployments ==========");
    System.out.println("Authentication: " + authentication);
    System.out.println(
        "Auth class: " +
            (authentication != null
                ? authentication.getClass().getName()
                : "NULL"));

    loadGitHubRepositories(model, oauthUser, authentication);

    return "deployments/list";
  }

  /**
   * Load GitHub repositories for the currently connected GitHub account.
   */
  private void loadGitHubRepositories(
      Model model,
      OAuth2User oauthUser,
      Authentication authentication) {

    try {

      if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
        System.out.println("No OAuth2 authentication found.");
        return;
      }

      String provider = oauthToken.getAuthorizedClientRegistrationId();

      System.out.println("OAuth provider: " + provider);
      System.out.println("OAuth name: " + oauthToken.getName());

      if (!"github".equals(provider)) {
        System.out.println("Provider is not GitHub.");
        return;
      }

      OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
          "github",
          oauthToken.getName());

      System.out.println("GitHub client: " + client);

      if (client == null) {
        System.out.println("GitHub authorized client is NULL.");
        return;
      }

      if (oauthUser == null) {
        System.out.println("GitHub OAuth user is NULL.");
        return;
      }

      String login = oauthUser.getAttribute("login");

      System.out.println("GitHub user: " + login);

      if (client.getAccessToken() == null) {
        System.out.println("GitHub access token is NULL.");
        return;
      }

      String token = client.getAccessToken().getTokenValue();

      RestTemplate restTemplate = new RestTemplate();

      HttpHeaders headers = new HttpHeaders();
      headers.setBearerAuth(token);
      headers.set(
          "Accept",
          "application/vnd.github+json");
      headers.set(
          "User-Agent",
          "OpenCloud-Dashboard");

      HttpEntity<String> request = new HttpEntity<>(headers);

      ResponseEntity<List> response = restTemplate.exchange(
          "https://api.github.com/user/repos"
              + "?per_page=100"
              + "&sort=updated"
              + "&affiliation=owner,collaborator,organization_member",
          HttpMethod.GET,
          request,
          List.class);

      List<Map> repositories = response.getBody();

      System.out.println(
          "GitHub API status: " +
              response.getStatusCode());

      System.out.println(
          "GitHub repos fetched: " +
              (repositories != null
                  ? repositories.size()
                  : 0));

      model.addAttribute(
          "githubRepos",
          repositories);

      model.addAttribute(
          "isGitHubConnected",
          true);

      model.addAttribute(
          "connectedUser",
          login != null
              ? login
              : oauthUser.getName());

      model.addAttribute(
          "providerInput",
          "GITHUB");

    } catch (Exception e) {

      System.out.println(
          "GitHub repository loading failed:");

      e.printStackTrace();

      model.addAttribute(
          "githubRepos",
          List.of());

      model.addAttribute(
          "isGitHubConnected",
          false);
    }
  }

  /**
   * Create deployment.
   */
  @PostMapping("/new")
  public String create(
      @RequestParam String repoUrl,
      @RequestParam String provider,
      @RequestParam String serverId,
      @RequestParam String buildType,
      @RequestParam(required = false) String envVars) {

    Deployment deployment = new Deployment();

    deployment.setName(
        repoUrl
            .substring(repoUrl.lastIndexOf('/') + 1)
            .replace(".git", ""));

    deployment.setRepoUrl(repoUrl);

    deployment.setProvider(
        Deployment.RepoProvider.valueOf(provider));

    deployment.setBuildType(
        Deployment.BuildType.valueOf(buildType));

    deployment.setServer(
        serverRepo.findById(serverId).orElse(null));

    if (envVars != null && !envVars.isBlank()) {

      Arrays.stream(envVars.split("\n"))
          .filter(line -> line.contains("="))
          .forEach(line -> {

            String[] keyValue = line.split("=", 2);

            deployment.getEnvVars().add(
                new EnvVar(
                    keyValue[0].trim(),
                    keyValue[1].trim(),
                    keyValue[0]
                        .toLowerCase()
                        .contains("secret")));
          });
    }

    deploymentRepo.save(deployment);

    orchestrator.triggerDeploy(
        deployment.getId(),
        "HEAD");

    return "redirect:/deployments/" + deployment.getId();
  }

  /**
   * Deployment details.
   */
  @GetMapping("/{id}")
  public String detail(
      @PathVariable String id,
      Model model) {

    model.addAttribute(
        "deployment",
        deploymentRepo.findById(id).orElseThrow());

    return "deployments/detail";
  }

  /**
   * Redeploy.
   */
  @PostMapping("/{id}/redeploy")
  public String redeploy(
      @PathVariable String id) {

    orchestrator.triggerDeploy(id, null);

    return "redirect:/deployments/" + id;
  }
}
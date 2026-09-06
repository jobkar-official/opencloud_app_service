
package dev.opencloud.presentation.api;

import dev.opencloud.application.service.DeployOrchestratorService;
import dev.opencloud.domain.entity.Deployment;
import dev.opencloud.domain.repository.DeploymentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/deployments")
public class DeploymentApiController {
  private final DeploymentRepository repo;
  private final DeployOrchestratorService orchestrator;

  public DeploymentApiController(DeploymentRepository r, DeployOrchestratorService o) {
    repo = r;
    orchestrator = o;
  }

  @GetMapping
  public List<Deployment> list() {
    return repo.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Deployment> get(@PathVariable String id) {
    return ResponseEntity.of(repo.findById(id));
  }

  @PostMapping("/{id}/deploy")
  public ResponseEntity<Deployment> deploy(@PathVariable String id,
      @RequestBody(required = false) Map<String, String> body) {
    String sha = body != null ? body.get("commitSha") : null;
    return ResponseEntity.ok(orchestrator.triggerDeploy(id, sha));
  }

  @PostMapping("/{id}/rollback/{versionId}")
  public ResponseEntity<?> rollback(@PathVariable String id, @PathVariable String versionId) {
    Deployment d = repo.findById(id).orElseThrow();
    d.getVersions().stream().filter(v -> v.getId().equals(versionId)).findFirst().ifPresent(v -> {
      d.setCommitSha(v.getCommitSha());
      orchestrator.triggerDeploy(d.getId(), v.getCommitSha());
    });
    return ResponseEntity.ok(Map.of("status", "rollback_triggered"));
  }
}

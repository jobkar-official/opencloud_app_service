
package dev.opencloud.application.service;

import dev.opencloud.domain.entity.*;
import dev.opencloud.domain.repository.*;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DeployOrchestratorService {
  private final DeploymentRepository deploymentRepo;
  private final ServerRepository serverRepo;
  // in-memory queue for VPS agent to pull (persisted status in DB)
  private final Map<String, Deployment> pendingDeployments = new ConcurrentHashMap<>();

  @Transactional
  public Deployment triggerDeploy(String deploymentId, String commitSha) {
    Deployment d = deploymentRepo.findById(deploymentId).orElseThrow();
    d.setCommitSha(commitSha != null ? commitSha : d.getCommitSha());
    d.setStatus(Deployment.Status.BUILDING);
    d.setLastDeployedAt(Instant.now());
    deploymentRepo.save(d);
    if (d.getServer() != null)
      pendingDeployments.put(d.getServer().getId(), d);
    return d;
  }

  @Transactional
  public void reportStatus(String deploymentId, String stage, String message, boolean healthOk) {
    Deployment d = deploymentRepo.findById(deploymentId).orElseThrow();
    switch (stage) {
      case "BUILDING" -> d.setStatus(Deployment.Status.BUILDING);
      case "DEPLOYING" -> d.setStatus(Deployment.Status.DEPLOYING);
      case "LIVE" -> {
        d.setStatus(healthOk ? Deployment.Status.LIVE : Deployment.Status.FAILED);
        if (healthOk) {
          DeployVersion v = new DeployVersion();
          v.setCommitSha(d.getCommitSha());
          v.setStatus(DeployVersion.Status.ACTIVE);
          v.setArtifactPath("/opt/opencloud/snapshots/" + d.getId() + "/" + UUID.randomUUID() + ".tar.gz");
          d.getVersions().add(0, v);
          // keep last 3
          if (d.getVersions().size() > 3)
            d.getVersions().subList(3, d.getVersions().size()).clear();
          pendingDeployments.remove(d.getServer().getId());
        } else {
          autoRollback(d);
        }
      }
      case "FAILED" -> {
        d.setStatus(Deployment.Status.FAILED);
        autoRollback(d);
      }
    }
    deploymentRepo.save(d);
  }

  private void autoRollback(Deployment d) {
    // find last SUCCESS version
    d.getVersions().stream()
        .filter(v -> v.getStatus() == DeployVersion.Status.SUCCESS || v.getStatus() == DeployVersion.Status.ACTIVE)
        .findFirst().ifPresent(lastGood -> {
          d.setStatus(Deployment.Status.ROLLED_BACK);
          // queue rollback as new pending
          pendingDeployments.put(d.getServer().getId(), d);
        });
  }

  public Deployment pollForServer(String serverId) {
    return pendingDeployments.get(serverId);
  }

  public void ackPulled(String serverId) {
    pendingDeployments.remove(serverId);
  }
}

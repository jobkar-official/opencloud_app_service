
package dev.opencloud.infrastructure.grpc;

import dev.opencloud.application.service.DeployOrchestratorService;
import dev.opencloud.domain.entity.Deployment;
import dev.opencloud.domain.entity.Server;
import dev.opencloud.domain.repository.ServerRepository;
import dev.opencloud.grpc.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import java.time.Instant;
import java.util.stream.Collectors;

@GrpcService
public class DeployGrpcService extends DeployServiceGrpc.DeployServiceImplBase {

  private final DeployOrchestratorService orchestrator;
  private final ServerRepository serverRepo;

  public DeployGrpcService(DeployOrchestratorService o, ServerRepository serverRepo) {
    this.orchestrator = o;
    this.serverRepo = serverRepo;
  }

  @Override
  public void registerAgent(RegisterAgentRequest req, StreamObserver<RegisterAgentResponse> obs) {
    Server s = serverRepo.findById(req.getServerId()).orElse(null);
    if (s != null) {
      s.setStatus(Server.Status.CONNECTED);
      s.setAgentVersion(req.getAgentVersion());
      s.setLastHeartbeat(Instant.now());
      serverRepo.save(s);
    }
    boolean accepted = s != null;
    obs.onNext(RegisterAgentResponse.newBuilder()
        .setAccepted(accepted)
        .setMessage(accepted ? "Registered " + req.getServerId() : "Unknown server id " + req.getServerId())
        .build());
    obs.onCompleted();
  }

  @Override
  public void heartbeat(HeartbeatRequest req, StreamObserver<HeartbeatResponse> obs) {
    serverRepo.findById(req.getServerId()).ifPresent(s -> {
      s.setLastHeartbeat(Instant.now());
      if (s.getStatus() != Server.Status.CONNECTED) {
        s.setStatus(Server.Status.CONNECTED);
      }
      serverRepo.save(s);
    });
    obs.onNext(HeartbeatResponse.newBuilder().setOk(true).build());
    obs.onCompleted();
  }

  @Override
  public void pullDeployment(PullDeploymentRequest req, StreamObserver<PullDeploymentResponse> obs) {
    Deployment d = orchestrator.pollForServer(req.getServerId());
    if (d == null) {
      obs.onNext(PullDeploymentResponse.newBuilder().setHasDeployment(false).build());
    } else {
      var b = PullDeploymentResponse.newBuilder()
          .setHasDeployment(true)
          .setDeploymentId(d.getId())
          .setRepoUrl(d.getRepoUrl())
          .setCommitSha(d.getCommitSha() != null ? d.getCommitSha() : "HEAD")
          .setBuildType(d.getBuildType().name());
      if (d.getEnvVars() != null)
        b.putAllEnvVars(d.getEnvVars().stream().collect(Collectors.toMap(ev -> ev.getKey(), ev -> ev.getValue())));
      obs.onNext(b.build());
      orchestrator.ackPulled(req.getServerId());
    }
    obs.onCompleted();
  }

  @Override
  public void reportDeployStatus(DeployStatusRequest req, StreamObserver<DeployStatusResponse> obs) {
    orchestrator.reportStatus(req.getDeploymentId(), req.getStage(), req.getMessage(), req.getHealthOk());
    obs.onNext(DeployStatusResponse.newBuilder().setAcknowledged(true).build());
    obs.onCompleted();
  }
}

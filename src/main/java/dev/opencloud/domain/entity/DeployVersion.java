
package dev.opencloud.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "deploy_versions")
@Data
@NoArgsConstructor
public class DeployVersion {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String commitSha;

  String artifactPath; // /opt/opencloud/artifacts/{id}.tar.gz
  @Enumerated(EnumType.STRING)

  Status status = Status.SUCCESS;

  Instant createdAt = Instant.now();

  String logSummary;

  public enum Status {
    SUCCESS, FAILED, ROLLED_BACK, ACTIVE
  }
}

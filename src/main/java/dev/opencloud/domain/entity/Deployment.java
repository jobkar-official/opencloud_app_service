
package dev.opencloud.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "deployments")
@Data
@NoArgsConstructor
public class Deployment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  private String name; // my-api

  @Enumerated(EnumType.STRING)
  private RepoProvider provider = RepoProvider.GITHUB;

  private String repoUrl;

  private String branch = "main";

  private String commitSha;

  @Enumerated(EnumType.STRING)
  private BuildType buildType = BuildType.NODE;

  @ManyToOne
  private Server server;

  @ManyToOne
  private User owner;

  @Enumerated(EnumType.STRING)
  private Status status = Status.CREATED;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "deployment_id")
  private List<EnvVar> envVars = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "deployment_id")
  private List<DeployVersion> versions = new ArrayList<>();

  private Instant createdAt = Instant.now();

  private Instant lastDeployedAt;

  public enum RepoProvider {
    GITHUB, GITLAB, BITBUCKET
  }

  public enum BuildType {
    NODE,
    NEXTJS,
    JAVA,
    PYTHON,
    GO
  }

  public enum Status {
    CREATED, BUILDING, DEPLOYING, LIVE, FAILED, ROLLED_BACK
  }
}


package dev.opencloud.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "deployments")
public class Deployment {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;
  String name; // my-api
  @Enumerated(EnumType.STRING)
  RepoProvider provider = RepoProvider.GITHUB;
  String repoUrl;
  String branch = "main";
  String commitSha;
  @Enumerated(EnumType.STRING)
  BuildType buildType = BuildType.NODE;
  @ManyToOne
  Server server;
  @ManyToOne
  User owner;
  @Enumerated(EnumType.STRING)
  Status status = Status.CREATED;
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "deployment_id")
  List<EnvVar> envVars = new ArrayList<>();
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "deployment_id")
  List<DeployVersion> versions = new ArrayList<>();
  Instant createdAt = Instant.now();
  Instant lastDeployedAt;

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

  // getters/setters
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String n) {
    this.name = n;
  }

  public RepoProvider getProvider() {
    return provider;
  }

  public void setProvider(RepoProvider p) {
    this.provider = p;
  }

  public String getRepoUrl() {
    return repoUrl;
  }

  public void setRepoUrl(String r) {
    this.repoUrl = r;
  }

  public String getBranch() {
    return branch;
  }

  public void setBranch(String b) {
    this.branch = b;
  }

  public String getCommitSha() {
    return commitSha;
  }

  public void setCommitSha(String s) {
    this.commitSha = s;
  }

  public BuildType getBuildType() {
    return buildType;
  }

  public void setBuildType(BuildType b) {
    this.buildType = b;
  }

  public Server getServer() {
    return server;
  }

  public void setServer(Server s) {
    this.server = s;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status s) {
    this.status = s;
  }

  public List<EnvVar> getEnvVars() {
    return envVars;
  }

  public List<DeployVersion> getVersions() {
    return versions;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getLastDeployedAt() {
    return lastDeployedAt;
  }

  public void setLastDeployedAt(Instant i) {
    this.lastDeployedAt = i;
  }
}

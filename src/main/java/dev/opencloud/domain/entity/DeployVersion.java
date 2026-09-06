
package dev.opencloud.domain.entity;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="deploy_versions")
public class DeployVersion {
  @Id @GeneratedValue(strategy=GenerationType.UUID) String id;
  String commitSha; String artifactPath; // /opt/opencloud/artifacts/{id}.tar.gz
  @Enumerated(EnumType.STRING) Status status = Status.SUCCESS;
  Instant createdAt = Instant.now();
  String logSummary;
  public enum Status { SUCCESS, FAILED, ROLLED_BACK, ACTIVE }
  public String getId(){return id;} public String getCommitSha(){return commitSha;} public void setCommitSha(String s){this.commitSha=s;}
  public String getArtifactPath(){return artifactPath;} public void setArtifactPath(String p){this.artifactPath=p;}
  public Status getStatus(){return status;} public void setStatus(Status s){this.status=s;}
  public Instant getCreatedAt(){return createdAt;}
}


package dev.opencloud.domain.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;
@Entity @Table(name="users")
public class User {
  @Id @GeneratedValue(strategy=GenerationType.UUID) String id;
  @Column(unique=true, nullable=false) String email;
  @Column(nullable=false) String passwordHash;
  String displayName;
  String avatarUrl;
  String oauthProvider; // GOOGLE, GITHUB, GITLAB, BITBUCKET
  String oauthId;
  @Enumerated(EnumType.STRING) Role role = Role.OWNER;
  @Enumerated(EnumType.STRING) Status status = Status.ACTIVE;
  Instant createdAt = Instant.now();
  Instant lastActiveAt;
  public enum Role { OWNER, ADMIN, DEVELOPER, VIEWER }
  public enum Status { ACTIVE, INVITED, CLOSED }
  // getters/setters
  public String getId(){return id;} public void setId(String id){this.id=id;}
  public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
  @JsonIgnore
  public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String h){this.passwordHash=h;}
  public String getDisplayName(){return displayName;} public void setDisplayName(String d){this.displayName=d;}
  public Role getRole(){return role;} public void setRole(Role r){this.role=r;}
  public Status getStatus(){return status;} public void setStatus(Status s){this.status=s;}
  public Instant getCreatedAt(){return createdAt;}
  public Instant getLastActiveAt(){return lastActiveAt;} public void setLastActiveAt(Instant i){this.lastActiveAt=i;}
  public String getOauthProvider(){return oauthProvider;} public void setOauthProvider(String p){this.oauthProvider=p;}
}

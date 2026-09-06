
package dev.opencloud.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  @Column(unique = true, nullable = false)
  String email;

  @Column(nullable = false)
  String passwordHash;

  String displayName;

  String phone;

  String country;

  String state;

  String city;

  String pincode;

  String avatarUrl;

  String oauthProvider;

  String oauthId;
  @Enumerated(EnumType.STRING)
  Role role = Role.OWNER;

  @Enumerated(EnumType.STRING)
  Status status = Status.ACTIVE;

  Instant createdAt = Instant.now();

  Instant lastActiveAt;

  public enum Role {
    OWNER, ADMIN, DEVELOPER, VIEWER
  }

  public enum Status {
    ACTIVE, INVITED, CLOSED
  }
}

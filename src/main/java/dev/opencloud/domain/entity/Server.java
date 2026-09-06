package dev.opencloud.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "servers")
@Data
@NoArgsConstructor
public class Server {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String name;

  String host;

  int sshPort = 22;

  String username = "root";
  @Column(length = 4000)
  String sshPrivateKeyRef;

  @Enumerated(EnumType.STRING)
  Status status = Status.DISCONNECTED;

  String agentVersion;

  Instant lastHeartbeat;

  String osInfo;

  @ManyToOne
  User owner;

  Instant createdAt = Instant.now();

  int totalDeployments = 0;

  public enum Status {
    CONNECTED, DISCONNECTED, CONNECTING, ERROR
  }
}
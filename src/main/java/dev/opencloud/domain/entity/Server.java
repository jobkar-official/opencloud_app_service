package dev.opencloud.domain.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "servers")
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

  // --- getters/setters ---
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

  public String getHost() {
    return host;
  }

  public void setHost(String h) {
    this.host = h;
  }

  public int getSshPort() {
    return sshPort;
  }

  public void setSshPort(int p) {
    this.sshPort = p;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String u) {
    this.username = u;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status s) {
    this.status = s;
  }

  public String getAgentVersion() {
    return agentVersion;
  }

  public void setAgentVersion(String v) {
    this.agentVersion = v;
  }

  public Instant getLastHeartbeat() {
    return lastHeartbeat;
  }

  public void setLastHeartbeat(Instant i) {
    this.lastHeartbeat = i;
  }

  public String getOsInfo() {
    return osInfo;
  }

  public void setOsInfo(String o) {
    this.osInfo = o;
  }

  public String getSshPrivateKeyRef() {
    return sshPrivateKeyRef;
  }

  public void setSshPrivateKeyRef(String r) {
    this.sshPrivateKeyRef = r;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User u) {
    this.owner = u;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Instant c) {
    this.createdAt = c;
  }

  public int getTotalDeployments() {
    return totalDeployments;
  }

  public void setTotalDeployments(int t) {
    this.totalDeployments = t;
  }
}
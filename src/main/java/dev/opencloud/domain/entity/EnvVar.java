
package dev.opencloud.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "env_vars")
@Data
@NoArgsConstructor
public class EnvVar {
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  String id;

  String key;

  String value;

  boolean secret = false;

  public EnvVar(String k, String v, boolean s) {
    key = k;
    value = v;
    secret = s;
  }
}

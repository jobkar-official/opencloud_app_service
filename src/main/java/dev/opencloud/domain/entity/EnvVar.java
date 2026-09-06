
package dev.opencloud.domain.entity;
import jakarta.persistence.*;
@Entity @Table(name="env_vars")
public class EnvVar {
  @Id @GeneratedValue(strategy=GenerationType.UUID) String id;
  String key; String value; boolean secret=false;
  public EnvVar(){}
  public EnvVar(String k,String v,boolean s){key=k;value=v;secret=s;}
  public String getKey(){return key;} public void setKey(String k){this.key=k;}
  public String getValue(){return value;} public void setValue(String v){this.value=v;}
  public boolean isSecret(){return secret;}
}

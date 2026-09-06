
package dev.opencloud.domain.repository;
import dev.opencloud.domain.entity.Deployment;
import dev.opencloud.domain.entity.Server;
import dev.opencloud.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface DeploymentRepository extends JpaRepository<Deployment,String> {
  List<Deployment> findByOwner(User owner);
  List<Deployment> findByServer(Server server);
  List<Deployment> findByServerId(String serverId);
}

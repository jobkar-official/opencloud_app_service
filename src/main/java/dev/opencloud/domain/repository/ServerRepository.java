
package dev.opencloud.domain.repository;
import dev.opencloud.domain.entity.Server;
import dev.opencloud.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ServerRepository extends JpaRepository<Server,String> {
  List<Server> findByOwner(User owner);
  List<Server> findByStatus(Server.Status status);
}


package dev.opencloud.domain.repository;
import dev.opencloud.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<User,String> {
  Optional<User> findByEmail(String email);
  Optional<User> findByOauthProviderAndOauthId(String provider,String oauthId);
}

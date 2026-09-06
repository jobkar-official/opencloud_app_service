
package dev.opencloud.config;

import dev.opencloud.domain.entity.*;
import dev.opencloud.domain.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;
import java.util.UUID;

@Configuration
public class DataSeeder {
  @Bean
  CommandLineRunner seed(UserRepository users, ServerRepository servers, DeploymentRepository deployments,
      PasswordEncoder encoder) {
    return args -> {
      if (users.count() > 0)
        return;
      User u = new User();
      u.setEmail("rahman@opencloud.dev");
      u.setPasswordHash(encoder.encode("changeme123"));
      u.setDisplayName("Rahman");
      u.setRole(User.Role.OWNER);
      u.setStatus(User.Status.ACTIVE);
      u.setLastActiveAt(Instant.now());
      users.save(u);

      User admin = new User();
      admin.setEmail("fahad.dev@gmail.com");
      admin.setPasswordHash(encoder.encode("changeme123"));
      admin.setDisplayName("Fahad Ansari");
      admin.setRole(User.Role.ADMIN);
      admin.setStatus(User.Status.ACTIVE);
      admin.setLastActiveAt(Instant.now().minusSeconds(7200));
      users.save(admin);

      User dev = new User();
      dev.setEmail("priya.nair@jobkar.in");
      dev.setPasswordHash(encoder.encode("changeme123"));
      dev.setDisplayName("Priya Nair");
      dev.setRole(User.Role.DEVELOPER);
      dev.setStatus(User.Status.ACTIVE);
      dev.setLastActiveAt(Instant.now().minusSeconds(86400));
      users.save(dev);

      User invited = new User();
      invited.setEmail("qa.contractor@gmail.com");
      invited.setPasswordHash(encoder.encode(UUID.randomUUID().toString()));
      invited.setDisplayName("Contract QA");
      invited.setRole(User.Role.VIEWER);
      invited.setStatus(User.Status.INVITED);
      users.save(invited);

      User closed = new User();
      closed.setEmail("intern.old@gmail.com");
      closed.setPasswordHash(encoder.encode(UUID.randomUUID().toString()));
      closed.setDisplayName("Former Intern");
      closed.setRole(User.Role.VIEWER);
      closed.setStatus(User.Status.CLOSED);
      closed.setLastActiveAt(Instant.now().minusSeconds(60L * 60 * 24 * 45));
      users.save(closed);

      Server s1 = new Server();
      s1.setName("prod-us-east-1");
      s1.setHost("3.85.12.44");
      s1.setStatus(Server.Status.CONNECTED);
      s1.setOwner(u);
      s1.setLastHeartbeat(Instant.now());
      servers.save(s1);

      Server s2 = new Server();
      s2.setName("staging-eu");
      s2.setHost("18.192.4.21");
      s2.setStatus(Server.Status.CONNECTED);
      s2.setOwner(u);
      s2.setLastHeartbeat(Instant.now().minusSeconds(120));
      servers.save(s2);

      Deployment d1 = new Deployment();
      d1.setName("opencloud-api");
      d1.setRepoUrl("https://github.com/jobkar-official/opencloud_api");
      d1.setProvider(Deployment.RepoProvider.GITHUB);
      d1.setBuildType(Deployment.BuildType.JAVA);
      d1.setServer(s1);
      d1.setStatus(Deployment.Status.LIVE);
      d1.setCommitSha("a1b2c3d");
      d1.getEnvVars().add(new EnvVar("PORT", "8081", false));
      d1.getEnvVars().add(new EnvVar("DATABASE_URL", "jdbc:postgresql://localhost:5432/opencloud", true));
      deployments.save(d1);
    };
  }
}

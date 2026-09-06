package dev.opencloud.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.opencloud.domain.entity.ClientSupport;

public interface ClientSupportRepos extends JpaRepository<ClientSupport, String> {

    List<ClientSupport> findByClientId(Long clientId);

    List<ClientSupport> findByClientIdAndStatus(Long clientId, String status);
}

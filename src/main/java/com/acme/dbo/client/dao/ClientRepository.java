package com.acme.dbo.client.dao;

import com.acme.dbo.client.domain.Client;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@ConditionalOnProperty(name = "features.client", havingValue = "true", matchIfMissing = true)
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByLogin(String login);
}
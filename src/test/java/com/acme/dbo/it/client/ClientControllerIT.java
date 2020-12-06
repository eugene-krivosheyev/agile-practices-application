package com.acme.dbo.it.client;

import com.acme.dbo.client.controller.ClientController;
import com.acme.dbo.client.domain.Client;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.time.Instant.now;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DisabledIf(expression = "#{environment['features.client'] == 'false'}", loadContext = true)
@SpringBootTest
@ActiveProfiles("preprod")
@Slf4j
@FieldDefaults(level = PRIVATE)
@Tag("docker")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class ClientControllerIT {
    @Autowired ClientController sut;

    // no need to create and init this container, @Testcontainers extension will make it based on connection string config at application.yml
    // @Container PostgreSQLContainer<?> pgFake = new PostgreSQLContainer<>("postgres:10-alpine").withDatabaseName("dbo-db").withUsername("dbo").withPassword("P@ssw0rd");

    @Test
    @Order(1)
    @Rollback(false)
    public void shouldMakeSideEffect() {
        sut.createClient(new Client(100L, "new@new.new", "new_secret", "new_salt", now(), true));
    }

    @Test
    @Order(2)
    public void shouldGetClientWhenSavedAsSideEffectOfPreviousTest() {
        assertThat(
                sut.getClients().stream().map(Client::getLogin).toArray()
        ).contains(
                "new@new.new"
        );
    }

    @Test
    public void shouldGetAllClientsWhenPrepopulatedDbHasSome() {
        assertThat(
                sut.getClients().stream().map(Client::getLogin).toArray()
        ).contains(
                "admin@acme.com",
                "account@acme.com",
                "disabled@acme.com"
        );
    }
}

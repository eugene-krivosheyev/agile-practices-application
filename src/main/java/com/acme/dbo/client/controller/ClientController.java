package com.acme.dbo.client.controller;

import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import com.acme.dbo.client.dao.ClientRepository;
import com.acme.dbo.client.domain.Client;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;
import java.util.Objects;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@ConditionalOnProperty(name = "features.client", havingValue = "true", matchIfMissing = true)
@RestController
@RequestMapping(value = "/api/client", headers = "X-API-VERSION=1")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
public class ClientController {
    @Autowired ClientRepository clients;
    @Autowired AccountRepository accounts;

    @ApiOperation(value = "Registration", notes = "Registered new user in service", response = Client.class)
    @ApiResponse(code = 201, message = "Client created")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Client createClient(@RequestBody @Valid final Client clientDto) {
        try {
            Client clientCreated = clients.saveAndFlush(clientDto);
            log.info("Client created #{}", clientCreated.getId());
            return clientCreated;
        } catch (Exception e) {
            log.error("Client creation error for client data: " + clientDto, e);
            throw e;
        }
    }

    @ApiOperation(value = "Info", notes = "Get all clients", response = Collection.class)
    @GetMapping
    public Collection<Client> getClients() {
        log.info("Client registry requested");
        return clients.findAll();
    }

    @ApiOperation(value = "Info", notes = "Get client information", response = Client.class)
    @GetMapping("/{id}")
    public Client getClient(@PathVariable("id") @PositiveOrZero long id) {
        log.info("Client data requested for client #{}", id);
        return clients.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Client #" + id));
    }

    @ApiOperation(value = "Deregistration by id", notes = "Delete client information")
    @DeleteMapping("/{id}")
    public void deleteClientById(@PathVariable("id") @PositiveOrZero long clientId) {
        try {
            accounts.findByClientId(clientId).stream()
                    .map(Account::getId)
                    .filter(Objects::nonNull)
                    .forEach(accountId -> {
                        accounts.deleteById(accountId);
                        log.info("Account deleted #{}", accountId);
                    });
            clients.deleteById(clientId);
            log.info("Client deleted #{}", clientId);
        } catch (Exception e) {
            log.error("Client deletion error for client #" + clientId, e);
            throw e;
        }
    }

    @ApiOperation(value = "Deregistration by login", notes = "Delete client information")
    @DeleteMapping("/login/{clientLogin}")
    public void deleteClientByLogin(@PathVariable("clientLogin") @NotEmpty @Email String clientLogin) {
        log.info("Client deleting with login #{}", clientLogin);
        try {
            clients.findByLogin(clientLogin).ifPresent(client -> {
                accounts.findByClientId(client.getId()).stream()
                        .map(Account::getId)
                        .filter(Objects::nonNull)
                        .forEach(accountId -> {
                            accounts.deleteById(accountId);
                            log.info("Account deleted #{}", accountId);
                        });
                clients.deleteById(client.getId());
                log.info("Client deleted #{}", client.getId());
            });
        } catch (Exception e) {
            log.error("Client deletion error for client with email #" + clientLogin, e);
            throw e;
        }
    }
}

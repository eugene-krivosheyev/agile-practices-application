package com.acme.dbo.it.bdd;

import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.client.dao.ClientRepository;
import com.acme.dbo.client.domain.Client;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assumptions.assumeFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * https://cucumber.io/docs/cucumber/cucumber-expressions/
 */
@DisabledIf(expression = "#{environment['features.client'] == 'false'}", loadContext = true)
@SpringBootTest
@CucumberContextConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class ClientBddSteps {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper jsonMapper;
    @Autowired ClientRepository clients;
    @Autowired AccountRepository accounts;

    private List<Client> foundClients;

    @Given("^application is running$")
    public void applicationIsRunning() throws Exception {
        mockMvc.perform(get("/actuator/health"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status", is("UP")));
    }

    @Given("^application has no stored clients$")
    public void applicationHasNoStoredClients() {
        accounts.deleteAll(); //Due to FK
        clients.deleteAll();
    }

    @When("^user requests all clients$")
    public void userRequestsAllClients() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                get("/api/client").header("X-API-VERSION", "1"))
                    .andExpect(status().isOk())
                    .andReturn().getResponse();

        foundClients = Arrays.asList(
                jsonMapper.readValue(
                    response.getContentAsString(),
                    Client[].class
        ));
    }

    @Then("^user got (.*)clients$")
    public void userGetClients(String howMany) {
        switch (howMany) {
            case "no " : assertThat(foundClients).isEmpty(); break;
            case "" : assertThat(foundClients).isNotNull(); break;
            default : assertThat(foundClients).hasSize(parseInt(howMany.trim())); break;
        }
    }

    @Given("application has stored clients")
    public void applicationHasStoredClients() {
        assumeFalse(clients.findAll().isEmpty());
    }

    @And("client stored with login {string}, secret {string}, salt {string}")
    public void storeClient(String login, String secret, String salt) throws Exception {
        Client client = Client.builder()
                .login(login)
                .secret(secret)
                .salt(salt)
            .build();

        mockMvc.perform(post("/api/client")
                .contentType("application/json")
                .content(jsonMapper.writeValueAsString(client))
                .header("X-API-VERSION", "1"))
            .andExpect(status().isCreated());
    }

    @And("client got with login {string}, secret {string}, salt {string}, {word}")
    public void gotClient(String login, String secret, String salt, String enabled) {
        assertThat(foundClients).contains(
                Client.builder()
                        .login(login)
                        .secret(secret)
                        .salt(salt)
                        .enabled("enabled".equals(enabled))
                .build()
        );
    }
}

package com.acme.dbo.it.account;

import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import com.acme.dbo.account.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;

import static com.acme.dbo.account.domain.Account.builder;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@DisabledIf(expression = "#{environment['features.account'] == 'false'}", loadContext = true)
@RestClientTest(AccountService.class)
@AutoConfigureWebClient(registerRestTemplate = true)
@ExtendWith(MockitoExtension.class)
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AccountServiceComponentIT {
    @Value("${spring.integration.legacyAccountingSystem.baseUrl}") String legacyAccountingSystemBaseUrl;
    @Value("${spring.integration.legacyAccountingSystem.accountsEndpoint}") String accountEndpoint;
    @Autowired AccountService sut;
    @Autowired ObjectMapper objectMapper;
    @Autowired MockRestServiceServer legacyServiceStub;
    @MockBean AccountRepository accountRepositoryStub;

    @BeforeEach
    public void setupAccountRepository() {
        given(accountRepositoryStub.findAll()).willReturn(new ArrayList<>(1));
    }

    @Test
    public void shouldGetAccountsWhenLegacyServiceHasOnes() throws JsonProcessingException {
        final Account accountStub = builder().id(-1L).clientId(-1L).amount(-1.).build();
        final Account[] accounts = { accountStub };

        legacyServiceStub
                .expect(requestTo(legacyAccountingSystemBaseUrl + accountEndpoint))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(accounts), MediaType.APPLICATION_JSON));

        assertThat(sut.getAccounts()).containsExactly(accountStub);
    }
}

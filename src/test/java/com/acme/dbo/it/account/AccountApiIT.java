package com.acme.dbo.it.account;

import com.acme.dbo.account.domain.Account;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;
import org.springframework.test.web.servlet.MockMvc;

import static com.acme.dbo.account.domain.Account.builder;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisabledIf(expression = "#{environment['features.account'] == 'false'}", loadContext = true)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AccountApiIT {
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper jsonMapper;

    @Test
    public void shouldGetAccountsWhenPrepopulatedDbHas() throws Exception {
        String accountsFoundJsonString = mockMvc.perform(
                get("/api/account").header("X-API-VERSION", "1")
        ).andDo(print()).andExpect(status().is(200))
                .andReturn().getResponse().getContentAsString();

        Account[] accountsFound = jsonMapper.readValue(accountsFoundJsonString, Account[].class);

        assertThat(accountsFound).contains(
                builder().clientId(1L).amount(0.).build(),
                builder().clientId(1L).amount(100.).build(),
                builder().clientId(2L).amount(200.).build()
        );
    }
}

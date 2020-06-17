package com.acme.dbo.account.service;

import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;

import static java.util.Collections.addAll;

@ConditionalOnProperty(name = "features.account", havingValue = "true", matchIfMissing = true)
@Service
@Slf4j
public class AccountService {
    @Value("${spring.integration.legacyAccountingSystem.baseUrl}") String legacyAccountingSystemBaseUrl;
    @Value("${spring.integration.legacyAccountingSystem.accountsEndpoint}") String accountEndpoint;

    @Autowired AccountRepository accountRepository;
    @Autowired RestTemplate legacyAccountingSystemRestTemplate;


    public Collection<Account> getAccounts() {
        final String legacyAccountingSystemAccountEndpoint = legacyAccountingSystemBaseUrl + accountEndpoint;

        Account[] accountsFromLegacyAccountingSystem = new Account[] {};
        try {
            log.debug("Requesting legacy accounting system");
            accountsFromLegacyAccountingSystem = legacyAccountingSystemRestTemplate.getForObject(legacyAccountingSystemAccountEndpoint, Account[].class);
        } catch (Exception e) {
            log.error("Legacy accounting system failed to respond for 'GET /api/account'. But it's main flow and totally Ok.");
            //but even so we must handle request anyway even with new data only
        }

        Collection<Account> accounts = accountRepository.findAll();
        addAll(accounts, accountsFromLegacyAccountingSystem);
        return accounts;
    }
}

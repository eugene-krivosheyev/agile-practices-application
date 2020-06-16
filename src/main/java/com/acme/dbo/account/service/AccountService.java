package com.acme.dbo.account.service;

import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Collection;

@ConditionalOnProperty(name = "features.account", havingValue = "true", matchIfMissing = true)
@Service
public class AccountService {
    @Autowired AccountRepository accountRepository;

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }
}

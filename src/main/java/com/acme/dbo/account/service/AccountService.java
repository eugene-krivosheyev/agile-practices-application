package com.acme.dbo.account.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "features.account", havingValue = "true", matchIfMissing = true)
@Service
public class AccountService {

}

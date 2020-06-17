package com.acme.dbo.account.controller;

import com.acme.dbo.account.domain.Account;
import com.acme.dbo.account.service.AccountService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PUBLIC;

@ConditionalOnProperty(name = "features.account", havingValue = "true", matchIfMissing = true)
@RestController
@RequestMapping(value = "/api/account", headers = "X-API-VERSION=1")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PUBLIC)
@Slf4j
public class AccountController {
    @Autowired AccountService accountService;

    @GetMapping
    @ApiOperation(value = "GetAccounts", notes = "Returned all created address of selected currency name")
    public Collection<Account> getAccounts() {
        return accountService.getAccounts();
    }
}

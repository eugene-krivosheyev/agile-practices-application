package com.acme.dbo.ut.account;

import com.acme.dbo.account.controller.AccountController;
import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import static java.util.Collections.singletonList;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisabledIf(expression = "#{environment['features.account'] == 'false'}", loadContext = true)
@ExtendWith(MockitoExtension.class)
@SpringBootTest //Have to load context in order to Feature Toggling expressions works
@ActiveProfiles("test")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AccountControllerUnitTest {
    AccountController sut;
    @Mock AccountRepository accountRepositoryMock;
    @Mock Account accountStub;

    @BeforeEach
    public void setupSut() {
        sut = new AccountController(accountRepositoryMock);
    }

    @Test
    public void shouldCallRepositoryAngGetAccountWhenMockedRepoHasOne() {
        given(accountRepositoryMock.findAll()).willReturn(singletonList(accountStub));

        assertThat(sut.getAccounts()).containsOnly(accountStub);
        verify(accountRepositoryMock, times(1)).findAll();
    }
}

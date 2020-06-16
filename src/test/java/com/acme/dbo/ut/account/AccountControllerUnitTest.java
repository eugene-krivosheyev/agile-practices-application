package com.acme.dbo.ut.account;

import com.acme.dbo.account.controller.AccountController;
import com.acme.dbo.account.domain.Account;
import com.acme.dbo.account.service.AccountService;
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
    @Mock AccountService accountServiceMock;
    @Mock Account accountStub;

    @BeforeEach
    public void setupSut() {
        sut = new AccountController(accountServiceMock);
    }

    @Test
    public void shouldCallRepositoryAngGetAccountWhenMockedRepoHasOne() {
        given(accountServiceMock.getAccounts()).willReturn(singletonList(accountStub));

        assertThat(sut.getAccounts()).containsOnly(accountStub);
        verify(accountServiceMock, times(1)).getAccounts();
    }
}

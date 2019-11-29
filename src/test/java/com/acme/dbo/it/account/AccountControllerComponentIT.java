package com.acme.dbo.it.account;

import com.acme.dbo.account.controller.AccountController;
import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.account.domain.Account;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@WebMvcTest(AccountController.class)
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class AccountControllerComponentIT {
    @Autowired AccountController sut;
    @MockBean AccountRepository accountRepositoryMock;
    @Mock Account accountStub;

    @Test
    public void shouldGetAccountWhenMockedDbHasOne() {
        given(accountRepositoryMock.findAll()).willReturn(singletonList(accountStub));

        assertThat(sut.getAccounts()).containsOnly(accountStub);
        verify(accountRepositoryMock, times(1)).findAll();
    }
}

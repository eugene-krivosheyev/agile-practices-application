package com.acme.dbo.it.client;

import com.acme.dbo.account.dao.AccountRepository;
import com.acme.dbo.client.controller.ClientController;
import com.acme.dbo.client.dao.ClientRepository;
import com.acme.dbo.client.service.ClientService;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.DisabledIf;

import javax.persistence.EntityNotFoundException;

import static java.util.Optional.empty;
import static lombok.AccessLevel.PRIVATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@DisabledIf(expression = "#{environment['features.client'] == 'false'}", loadContext = true)
@ExtendWith(MockitoExtension.class)
@WebMvcTest(ClientController.class)
@ActiveProfiles("it")
@Slf4j
@FieldDefaults(level = PRIVATE)
public class ClientControllerComponentIT {
    @Autowired ClientController sut;
    @MockBean AccountRepository accountRepositoryMock;
    @MockBean ClientRepository clientRepositoryMock;

    @Test
    public void shouldGetErrorWhenGetNonExistentClient() {
        given(clientRepositoryMock.findById(anyLong())).willReturn(empty());

        final EntityNotFoundException exceptionThrown = assertThrows(EntityNotFoundException.class,
                () -> sut.getClient(1));

        assertThat(exceptionThrown.getMessage())
                .isNotEmpty()
                .isEqualTo("Client #1");
    }
}

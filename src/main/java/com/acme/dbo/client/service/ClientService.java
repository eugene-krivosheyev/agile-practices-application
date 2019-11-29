package com.acme.dbo.client.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "features.client", havingValue = "true", matchIfMissing = true)
@Service
public class ClientService {
}

package dev.opencloud.domain.service;

import org.springframework.stereotype.Service;

import dev.opencloud.domain.repository.ClientSupportRepos;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientSupportService {

    private final ClientSupportRepos clientSupportRepos;

}

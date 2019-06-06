package me.exrates.adminservice.core.service;

import org.springframework.transaction.annotation.Transactional;

public interface SyncUserService {

    @Transactional
    void syncUsers();
}

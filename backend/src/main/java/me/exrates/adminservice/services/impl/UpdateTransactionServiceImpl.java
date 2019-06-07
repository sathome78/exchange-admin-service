package me.exrates.adminservice.services.impl;

import lombok.extern.log4j.Log4j2;
import me.exrates.adminservice.services.UpdateTransactionService;
import org.springframework.stereotype.Service;

@Service
@Log4j2
public class UpdateTransactionServiceImpl implements UpdateTransactionService {


    @Override
    public boolean onUpdate() {
        log.error("Updated transactions");
        return false;
    }
}

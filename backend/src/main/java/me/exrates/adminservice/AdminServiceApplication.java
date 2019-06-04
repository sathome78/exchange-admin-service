package me.exrates.adminservice;

import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.UUID;

@SpringBootApplication
@Log4j2
public class AdminServiceApplication{

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminServiceApplication.class);


    public static void main(String[] args) {
        String value = UUID.randomUUID().toString();
        log.trace("doStuff needed more information - {}", value);
        log.debug("doStuff needed to debug - {}", value);
        log.info("doStuff took input - {}", value);
        log.warn("doStuff needed to warn - {}", value);

        SpringApplication.run(AdminServiceApplication.class, args);
    }

}

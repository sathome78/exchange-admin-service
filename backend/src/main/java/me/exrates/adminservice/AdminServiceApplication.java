package me.exrates.adminservice;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Log4j2
public class AdminServiceApplication{


    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }

}

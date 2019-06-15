package me.exrates.adminservice.configurations;

import lombok.extern.log4j.Log4j2;
import me.exrates.SSMGetter;
import me.exrates.SSMGetterImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class SSMConfiguration {

    @Bean
    @ConditionalOnProperty(value = "ssm.develop-mode", havingValue = "false", matchIfMissing = true)
    public SSMGetter ssmGetter() {
        return new SSMGetterImpl();
    }

    @Bean("ssmGetter")
    @ConditionalOnProperty(value = "ssm.develop-mode", havingValue = "true")
    public SSMGetter mockSSM() {
        return new MockSSM();
    }

    private class MockSSM implements SSMGetter {

        @Value("${db-admin.datasource.password}")
        private String adminPassword;

        @Value("${db-core.datasource.password}")
        private String corePassword;

        MockSSM() {
            log.info("Using mock ssm lookup...");
        }

        @Override
        public String lookup(String path) {
            return path.equalsIgnoreCase("admin") ? adminPassword : corePassword;
        }
    }
}
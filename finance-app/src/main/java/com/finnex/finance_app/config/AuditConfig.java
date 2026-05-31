package com.finnex.finance_app.config;


import com.finnex.finance_app.common.audit.AuditAwareImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditAware")
public class AuditConfig {
    @Bean
    public AuditAwareImpl auditAware() {
        return new AuditAwareImpl();
    }
}

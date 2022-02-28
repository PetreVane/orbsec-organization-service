package com.orbsec.organizationservice.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "spring")
@Getter
@Setter
public class ServiceConfig {

    @Value("${spring.application.name:null}")
    private String applicationName;

    @Value("${logging.logstash.url}")
    private String logstashUrl;

}

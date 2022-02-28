package com.orbsec.organizationservice.logging;

import ch.qos.logback.classic.LoggerContext;
import com.orbsec.organizationservice.configs.ServiceConfig;
import net.logstash.logback.appender.LogstashTcpSocketAppender;
import net.logstash.logback.encoder.LogstashEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class LogstashAppender {

    private final ServiceConfig serviceConfig;

    @Autowired
    public LogstashAppender(ServiceConfig serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshedEvent(ContextRefreshedEvent event) {
        this.addLogStashAppenderIfMissing();
    }

    @EventListener(RefreshScopeRefreshedEvent.class)
    public void onRefreshScopeRefreshedEvent(RefreshScopeRefreshedEvent event) {
        this.addLogStashAppenderIfMissing();
    }

    @EventListener(EnvironmentChangeEvent.class)
    public void onEnvironmentChangeEvent(EnvironmentChangeEvent event) {
        this.addLogStashAppenderIfMissing();
    }

    public void addLogStashAppenderIfMissing() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        synchronized (this) {
            if (Objects.isNull(loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).getAppender("LOGSTASH"))) {
                LogstashTcpSocketAppender logstashTcpSocketAppender = new LogstashTcpSocketAppender();
                logstashTcpSocketAppender.setName("LOGSTASH");
                logstashTcpSocketAppender.setContext(loggerContext);
                logstashTcpSocketAppender.addDestination(serviceConfig.getLogstashUrl());

                LogstashEncoder encoder = new LogstashEncoder();
                encoder.setIncludeMdc(true);
                encoder.getFieldNames().setLevelValue(null);
                encoder.setCustomFields(String.format("{\"app_name\":\"%s\"}", serviceConfig.getApplicationName()));

                logstashTcpSocketAppender.setEncoder(encoder);
                logstashTcpSocketAppender.start();

                loggerContext.getLogger(Logger.ROOT_LOGGER_NAME).addAppender(logstashTcpSocketAppender);

            }
        }
    }
}

package com.mport;

import com.mport.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableCaching
@EnableConfigurationProperties(AppProperties.class)
@SpringBootApplication
public class PortAnalystBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortAnalystBackendApplication.class, args);
    }

}

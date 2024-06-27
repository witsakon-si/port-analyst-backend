package com.mport;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableScheduling
@EnableCaching
@EnableSwagger2
@SpringBootApplication
public class PortAnalystBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortAnalystBackendApplication.class, args);
    }

}

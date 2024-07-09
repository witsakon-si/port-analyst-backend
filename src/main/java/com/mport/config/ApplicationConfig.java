package com.mport.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mport.domain.dto.ApplicationDTO;
import com.mport.domain.mapper.HistoryMapper;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Connector;
import org.modelmapper.ModelMapper;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AppProperties appProperties;

    @Bean
    public DataSource dataSource() {
        final DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(appProperties.getDatasource().getDriverClassName());
        dataSource.setUrl(appProperties.getDatasource().getUrl());
        dataSource.setUsername(appProperties.getDatasource().getUsername());
        dataSource.setPassword(appProperties.getDatasource().getPassword());
        return dataSource;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addMappings(new HistoryMapper());
        return modelMapper;
    }

    @Bean
    public ServletWebServerFactory servletWebServerFactory() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
        Connector connector = new Connector(TomcatServletWebServerFactory.DEFAULT_PROTOCOL);
        connector.setPort(8081);
        tomcat.addAdditionalTomcatConnectors(connector);
        return tomcat;
    }

    @Bean
    public ApplicationDTO applicationDTO() {
        return new ApplicationDTO();
    }

    @Bean
    public ObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        return mapper;
    }
}

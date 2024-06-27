package com.mport.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.HazelcastInstance;
import com.mport.domain.dto.ApplicationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PriceScheduler {

    private final HazelcastInstance hz;
    private final ApplicationDTO applicationCache;
    private final ObjectMapper mapper;

    private PriceSocketClient priceSocketClient;

    @Value("${api.key}")
    private String API_KEY;

    @Autowired
    public PriceScheduler(ApplicationDTO applicationCache, HazelcastInstance hz, ObjectMapper mapper) {
        this.applicationCache = applicationCache;
        this.hz = hz;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelay = 25000)      // ping every 25s
    public void cronScheduleTask() {
        try {
            if (priceSocketClient == null || !priceSocketClient.getClientSession().isOpen()) {
                priceSocketClient = new PriceSocketClient(applicationCache, mapper, hz, API_KEY);
            }
        } catch (Exception e) {
            log.error("Error PriceScheduler: {}", e.getMessage());
            e.printStackTrace();
        }
    }
}

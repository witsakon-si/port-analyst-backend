package com.mport.config;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HazelcastConfiguration {

    @Bean
    public HazelcastInstance hazelcastInstance() {

        Config config = new Config();
        MapConfig mapConfig = new MapConfig();
        mapConfig.setName("hazelcast-config");
        mapConfig.setEvictionConfig(new EvictionConfig()
                .setSize(200)
                .setMaxSizePolicy(MaxSizePolicy.FREE_HEAP_SIZE)
                .setEvictionPolicy(EvictionPolicy.LRU)
        );
        config.setInstanceName("INSTANCE_1");
        config.addMapConfig(mapConfig);
        return Hazelcast.newHazelcastInstance(config);
    }
}

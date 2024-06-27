package com.mport.system;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import com.mport.domain.dto.ApplicationDTO;
import com.mport.domain.dto.FxPriceDTO;
import com.mport.domain.service.AssetInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Slf4j
@Component
public class StartUp {

    private final ApplicationDTO applicationCache;
    private final HazelcastInstance hz;
    private final SimpMessagingTemplate template;
    private final AssetInfoService assetInfoService;

    @Autowired
    public StartUp(ApplicationDTO applicationDTO, HazelcastInstance hz, SimpMessagingTemplate template, AssetInfoService assetInfoService) {
        this.applicationCache = applicationDTO;
        this.hz = hz;
        this.template = template;
        this.assetInfoService = assetInfoService;
    }

    @PostConstruct
    public void init() {
        log.info("--------on startup--------");
        try {
            applicationCache.setAssetRefTh(assetInfoService.loadAssetRefTh());

            IMap<String, FxPriceDTO> map = hz.getMap("REALTIME_PRICE");
            map.addEntryListener(new RealtimePriceEntryListener(template), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

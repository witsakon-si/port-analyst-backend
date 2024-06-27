package com.mport.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hazelcast.core.EntryEvent;
import com.hazelcast.map.listener.EntryAddedListener;
import com.hazelcast.map.listener.EntryRemovedListener;
import com.hazelcast.map.listener.EntryUpdatedListener;
import com.mport.domain.dto.FxPriceDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Slf4j
public class RealtimePriceEntryListener implements EntryAddedListener<String, FxPriceDTO>,
        EntryUpdatedListener<String, FxPriceDTO>,
        EntryRemovedListener<String, FxPriceDTO> {

    private SimpMessagingTemplate template;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public RealtimePriceEntryListener(SimpMessagingTemplate template) {
        this.template = template;
    }

    @Override
    public void entryAdded(EntryEvent<String, FxPriceDTO> event) {
        pushPrice(event.getValue());
    }

    @Override
    public void entryRemoved(EntryEvent<String, FxPriceDTO> event) {
    }

    @Override
    public void entryUpdated(EntryEvent<String, FxPriceDTO> event) {
        pushPrice(event.getValue());
    }

    public void pushPrice(FxPriceDTO priceDTO) {
        try {
            template.convertAndSend("/topic/realtime-price", objectMapper.writeValueAsString(priceDTO));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

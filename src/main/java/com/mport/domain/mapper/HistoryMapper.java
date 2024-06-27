package com.mport.domain.mapper;

import com.mport.domain.dto.HistoryDTO;
import com.mport.domain.model.History;

import org.modelmapper.PropertyMap;

public class HistoryMapper extends PropertyMap<HistoryDTO, History> {

    @Override
    protected void configure() {
        skip(destination.getId());
    }
}

package com.mport.domain.service;

import com.mport.domain.model.AssetActive;
import com.mport.domain.repository.AssetActiveRepository;
import com.mport.domain.util.DateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AssetActiveService {

    private final AssetActiveRepository assetActiveRepository;
    private final ModelMapper modelMapper;

    public AssetActiveService(AssetActiveRepository assetActiveRepository, ModelMapper modelMapper) {
        this.assetActiveRepository = assetActiveRepository;
        this.modelMapper = modelMapper;
    }

    public void setActiveAsset(Set<String> activeAsset) {
        log.info("setActiveAsset: {} ", activeAsset);
        Date now = DateTimeUtil.now();

        List<AssetActive> assetActiveList = new ArrayList<>();
        for (String asset : activeAsset) {
            AssetActive assetActive = new AssetActive();
            assetActive.setName(asset);
            assetActiveList.add(assetActive);
        }
        assetActiveRepository.saveAll(assetActiveList);
    }

    public void clearTable() {
        assetActiveRepository.deleteAll();
    }

    public List<AssetActive> getAllAssetActive() {
        log.info("getAllAssetActive");
        return assetActiveRepository.findAll();
    }
}

package com.mport.domain.service;

import com.mport.domain.dto.AssetInfoDTO;
import com.mport.domain.dto.AssetRefThDTO;
import com.mport.domain.exception.DataDuplicateException;
import com.mport.domain.exception.DataNotFoundException;
import com.mport.domain.model.AssetInfo;
import com.mport.domain.repository.AssetInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AssetInfoService {

    private final AssetInfoRepository assetInfoRepository;
    private final ModelMapper modelMapper;

    public AssetInfoService(AssetInfoRepository assetInfoRepository, ModelMapper modelMapper) {
        this.assetInfoRepository = assetInfoRepository;
        this.modelMapper = modelMapper;
    }

    public List<AssetInfoDTO> getAssetInfoList() {
        log.info("getAssetInfoList");
        List<AssetInfo> assetInfos = assetInfoRepository.findAll();
        List<AssetInfoDTO> assetInfoDTOS = modelMapper.map(assetInfos, new TypeToken<List<AssetInfoDTO>>() {
        }.getType());
        assetInfoDTOS.sort(new Comparator<AssetInfoDTO>() {
            public int compare(AssetInfoDTO o1, AssetInfoDTO o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        return assetInfoDTOS;
    }

    public List<AssetInfoDTO> getRefAssetInfo() {
        log.info("getRefAssetInfo");
        List<AssetInfo> assetInfos = assetInfoRepository.findByRefNameIsNotNull();
        List<AssetInfoDTO> assetInfoDTOS = modelMapper.map(assetInfos, new TypeToken<List<AssetInfoDTO>>() {
        }.getType());
        return assetInfoDTOS;
    }

    public AssetInfoDTO getRefAssetInfo(String name) {
        log.info("getRefAssetInfo: {}", name);
        AssetInfo assetInfo = assetInfoRepository.findByNameAndRefNameIsNotNull(name);
        if (assetInfo != null) {
            return modelMapper.map(assetInfo, AssetInfoDTO.class);
        }
        return null;
    }

    public List<AssetInfoDTO> getFullNameAssetInfo() {
        log.info("getFullNameAssetInfo");
        List<AssetInfo> assetInfos = assetInfoRepository.findByFullNameIsNotNull();
        List<AssetInfoDTO> assetInfoDTOS = modelMapper.map(assetInfos, new TypeToken<List<AssetInfoDTO>>() {
        }.getType());
        return assetInfoDTOS;
    }

    public List<String> getViAssetNames() {
        log.info("getViAssetNames");
        List<AssetInfo> assetInfos = assetInfoRepository.findByVi(true);
        return assetInfos.stream().map(AssetInfo::getName).collect(Collectors.toList());
    }

    public void saveAssetInfo(AssetInfoDTO assetInfoDTO) throws DataDuplicateException {
        log.info("saveAssetInfo");
        AssetInfo assetInfo = assetInfoRepository.findTopByName(assetInfoDTO.getName());
        if (assetInfo == null) {        // add new
            assetInfo = new AssetInfo();
        } else {
            if (!assetInfoDTO.getIsEdit()) {
                throw new DataDuplicateException("Data name duplicate");
            }
        }
        modelMapper.map(assetInfoDTO, assetInfo);
        assetInfoRepository.save(assetInfo);
    }

    public void deleteAssetInfo(String name) throws DataNotFoundException {
        log.info("deleteAssetInfo");
        AssetInfo assetInfo = assetInfoRepository.findTopByName(name);
        if (assetInfo != null) {
            assetInfoRepository.delete(assetInfo);
        } else {
            throw new DataNotFoundException("Asset Info for {" + name + "} doesn't exist.");
        }
    }



    public String getChartURL(String asset) {
        log.info("getChartURL: {} ", asset);
        AssetInfo assetInfo = assetInfoRepository.findTopByName(asset);
        String chartURL = "";
        if (assetInfo != null) {
            if (assetInfo.getUrl() != null) {
                chartURL = assetInfo.getUrl();
            } else if (assetInfo.getRefURL() != null) {
                chartURL = assetInfo.getRefURL();
            }
        }
        return chartURL;
    }

    public String getChartRefURL(String asset) {
        log.info("getChartRefURL: {} ", asset);
        AssetInfo assetInfo = assetInfoRepository.findTopByName(asset);
        return assetInfo == null ? "" : assetInfo.getRefURL();
    }

    public Map<String, AssetRefThDTO> loadAssetRefTh() {
        Map<String, AssetRefThDTO> assetRefThMap = new HashMap<>();
        List<AssetInfo> assetInfos = assetInfoRepository.findByRefNameIsNotNull();
        for (AssetInfo assetInfo : assetInfos) {
            String name = assetInfo.getRefName();
            String symbol = name.split("\\(")[0].split(":")[0];
            String market = name.split("\\(")[0].split(":")[1];
            String divisor = name.split("\\(")[1].split("\\)")[0].split(":")[1];
            String curr = name.split("\\[")[1].replaceAll("]", "").trim();

            AssetRefThDTO assetRefThDTO = new AssetRefThDTO();
            assetRefThDTO.setSymbolTh(assetInfo.getName());
            assetRefThDTO.setSymbol(symbol);
            assetRefThDTO.setMarket(market);
            assetRefThDTO.setDivisor(new BigDecimal(divisor));
            assetRefThDTO.setCurrency(curr+"-THB");

            assetRefThMap.put(symbol, assetRefThDTO);
        }
        return assetRefThMap;
    }

}

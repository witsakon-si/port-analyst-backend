package com.mport.domain.repository;

import com.mport.domain.model.DailyAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface DailyAssetRepository extends JpaRepository<DailyAsset, Long>, JpaSpecificationExecutor<DailyAsset> {
    List<DailyAsset> findByType(String type);
    List<DailyAsset> findByTypeAndName(String type, String name);
}

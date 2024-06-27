package com.mport.domain.repository;

import com.mport.domain.model.AssetInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AssetInfoRepository extends JpaRepository<AssetInfo, Long>, JpaSpecificationExecutor<AssetInfo> {
    AssetInfo findTopByName(String name);
    List<AssetInfo> findByRefNameIsNotNull();
    List<AssetInfo> findByFullNameIsNotNull();
    List<AssetInfo> findByVi(Boolean isVi);
    AssetInfo findByNameAndRefNameIsNotNull(String name);
}

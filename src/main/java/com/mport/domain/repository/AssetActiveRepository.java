package com.mport.domain.repository;

import com.mport.domain.model.AssetActive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AssetActiveRepository extends JpaRepository<AssetActive, Long>, JpaSpecificationExecutor<AssetActive> {
}


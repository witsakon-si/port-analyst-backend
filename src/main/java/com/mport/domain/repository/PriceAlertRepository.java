package com.mport.domain.repository;

import com.mport.domain.model.PriceAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface PriceAlertRepository extends JpaRepository<PriceAlert, String>, JpaSpecificationExecutor<PriceAlert> {
    List<PriceAlert> findByActiveOrderBySymbolAscConditionDescPriceAsc(Boolean active);
    Optional<PriceAlert> findByIdAndActive(Long id, Boolean active);
}

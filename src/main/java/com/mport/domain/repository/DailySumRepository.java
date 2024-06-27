package com.mport.domain.repository;

import com.mport.domain.model.DailySum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DailySumRepository extends JpaRepository<DailySum, Long>, JpaSpecificationExecutor<DailySum> {

}

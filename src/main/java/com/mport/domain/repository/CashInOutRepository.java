package com.mport.domain.repository;

import com.mport.domain.model.CashInOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CashInOutRepository extends JpaRepository<CashInOut, Long>, JpaSpecificationExecutor<CashInOut> {
    Optional<CashInOut> findByIdAndActive(long id, boolean active);
    List<CashInOut> findByActiveOrderByAccount(boolean active);
    List<CashInOut> findByAccountAndActiveOrderByTransactionDateAsc(String account, boolean active);
}


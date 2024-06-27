package com.mport.domain.repository;

import com.mport.domain.model.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long>, JpaSpecificationExecutor<History> {

    Optional<History> findByIdAndActive(long id, boolean isActive);

    List<History> findByActiveOrderByTypeAscNameAscOrderMatchDescTransactionDateAsc(boolean isActive);
    List<History> findByActiveAndNameInOrderByNameAscTransactionDateAsc(boolean isActive, List<String> names);
    List<History> findByIdIsIn(List<Long> id);
    List<History> findByOrderMatch(String matchOrderId);

}

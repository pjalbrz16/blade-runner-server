package org.unamur.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.unamur.persistence.PrScanResult;

import java.util.Optional;

@Repository
public interface PrScanResultRepository extends JpaRepository<PrScanResult, Long> {

    Optional<PrScanResult> findByPrIdAndProjectUrl(String prId, String projectUrl);
}

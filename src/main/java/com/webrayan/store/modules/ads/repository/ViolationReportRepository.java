package com.webrayan.store.modules.ads.repository;

import com.webrayan.store.modules.ads.entity.AdViolationReport;
//import com.webrayan.store.enums.ViolationReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ViolationReportRepository extends JpaRepository<AdViolationReport, Long> {
//    List<ViolationReport> findByStatus(ViolationReportStatus status);
    List<AdViolationReport> findByReportedUserId(Long reportedUserId);
    List<AdViolationReport> findByReportedAdId(Long reportedAdId);
}

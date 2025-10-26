package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.ads.entity.AdViolationReport;
//import com.webrayan.commerce.enums.ViolationReportStatus;
import com.webrayan.commerce.modules.ads.repository.ViolationReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ViolationReportService {

    private final ViolationReportRepository repository;

    public ViolationReportService(ViolationReportRepository repository) {
        this.repository = repository;
    }

    public List<AdViolationReport> getAllReports() {
        return repository.findAll();
    }

    public Optional<AdViolationReport> getReportById(Long id) {
        return repository.findById(id);
    }

//    public List<ViolationReport> getReportsByStatus(ViolationReportStatus status) {
//        return repository.findByStatus(status);
//    }

    public List<AdViolationReport> getReportsByUserId(Long userId) {
        return repository.findByReportedUserId(userId);
    }

    public List<AdViolationReport> getReportsByAdId(Long adId) {
        return repository.findByReportedAdId(adId);
    }

    public AdViolationReport saveReport(AdViolationReport report) {
        if (report.getReason() == null || report.getReason().trim().isEmpty()) {
            throw new IllegalArgumentException("Reason cannot be null or empty.");
        }
        return repository.save(report);
    }

//    public void updateStatus(Long reportId, ViolationReportStatus status) {
//        ViolationReport report = repository.findById(reportId)
//                .orElseThrow(() -> new IllegalArgumentException("Report not found."));
//        report.setStatus(status);
//        repository.save(report);
//    }

    public void deleteReport(Long id) {
        repository.deleteById(id);
    }
}

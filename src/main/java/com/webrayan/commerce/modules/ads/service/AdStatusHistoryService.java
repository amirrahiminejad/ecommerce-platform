package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.ads.entity.AdStatusHistory;
import com.webrayan.commerce.modules.ads.repository.AdStatusHistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdStatusHistoryService {

    private final AdStatusHistoryRepository repository;

    public AdStatusHistoryService(AdStatusHistoryRepository repository) {
        this.repository = repository;
    }

    public List<AdStatusHistory> getStatusHistoryByAdId(Long adId) {
        return repository.findByAdId(adId);
    }

    public AdStatusHistory saveStatusHistory(AdStatusHistory adStatusHistory) {
        return repository.save(adStatusHistory);
    }
}

package com.webrayan.store.modules.ads.controller;

import com.webrayan.store.modules.ads.entity.AdStatusHistory;
import com.webrayan.store.modules.ads.service.AdStatusHistoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ad-status-history")
public class AdStatusHistoryController {

    private final AdStatusHistoryService service;

    public AdStatusHistoryController(AdStatusHistoryService service) {
        this.service = service;
    }

    @GetMapping("/{adId}")
    public List<AdStatusHistory> getAdStatusHistory(@PathVariable Long adId) {
        return service.getStatusHistoryByAdId(adId);
    }

    @PostMapping
    public AdStatusHistory saveAdStatusHistory(@RequestBody AdStatusHistory adStatusHistory) {
        return service.saveStatusHistory(adStatusHistory);
    }
}

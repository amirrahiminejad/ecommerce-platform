package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.modules.ads.entity.AdPayment;
import com.webrayan.commerce.core.common.enums.PaymentStatus;
import com.webrayan.commerce.modules.ads.repository.AdPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AdPaymentService {

    private final AdPaymentRepository repository;

    @Autowired
    private LogService logService;

    public AdPaymentService(AdPaymentRepository repository) {
        this.repository = repository;
    }

    public List<AdPayment> getPaymentsByUserId(Long userId) {
        List<AdPayment> list = repository.findByUserId(userId);
        logService.log("PaymentService", "getPaymentsByUserId", "Fetched payments for user id: " + userId);
        return list;
    }

    public List<AdPayment> getPaymentsByAdId(Long adId) {
        List<AdPayment> list = repository.findByAdId(adId);
        logService.log("PaymentService", "getPaymentsByAdId", "Fetched payments for ad id: " + adId);
        return list;
    }

    public Optional<AdPayment> getPaymentById(Long id) {
        Optional<AdPayment> payment = repository.findById(id);
        logService.log("PaymentService", "getPaymentById", "Fetched payment id: " + id);
        return payment;
    }

    public AdPayment savePayment(AdPayment adPayment) {
        if (adPayment.getAmount() <= 0) {
            logService.log("PaymentService", "savePayment", "Invalid payment amount: " + adPayment.getAmount());
            throw new IllegalArgumentException("Payment amount must be greater than zero.");
        }
        AdPayment saved = repository.save(adPayment);
        logService.log("PaymentService", "savePayment", "Saved payment id: " + saved.getId());
        return saved;
    }

    public void updatePaymentStatus(Long paymentId, PaymentStatus status) {
        AdPayment adPayment = repository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
        adPayment.setStatus(status);
        if (status == PaymentStatus.COMPLETED) {
            adPayment.setPaidAt(new Date());
        }
        repository.save(adPayment);
        logService.log("PaymentService", "updatePaymentStatus", "Updated payment id: " + paymentId + " to status: " + status);
    }
}

package com.webrayan.commerce.modules.ads.service;

import com.webrayan.commerce.core.common.entity.LogEntry;
import com.webrayan.commerce.modules.ads.repository.LogEntryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LogService {
    @Autowired
    private LogEntryRepository logEntryRepository;

    public void log(String service, String operation, String message) {
        LogEntry entry = new LogEntry();
        entry.setService(service);
        entry.setOperation(operation);
        entry.setMessage(message);
        try {
            String username = SecurityContextHolder.getContext().getAuthentication() != null ?
                    SecurityContextHolder.getContext().getAuthentication().getName() : null;
            entry.setUsername(username);
        } catch (Exception e) {
            entry.setUsername(null);
        }
        logEntryRepository.save(entry);
    }
}

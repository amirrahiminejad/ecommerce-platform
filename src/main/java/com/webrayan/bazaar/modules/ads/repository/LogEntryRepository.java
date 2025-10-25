package com.webrayan.bazaar.modules.ads.repository;

import com.webrayan.bazaar.core.common.entity.LogEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogEntryRepository extends JpaRepository<LogEntry, Long> {
}

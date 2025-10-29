package com.webrayan.store.core.common.repository;

import com.webrayan.store.core.common.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    List<Setting> findByUserId(Long userId);
    Setting findByKeyAndUserId(String key, Long userId);
    boolean existsByKey(String key);
    Optional<Setting> findByKey(String key);
}

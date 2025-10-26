package com.webrayan.commerce.core.common.repository;

import com.webrayan.commerce.core.common.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    List<Setting> findByUserId(Long userId);
    Setting findByKeyAndUserId(String key, Long userId);
    boolean existsByKey(String key);
    Setting findByKey(String key);
}

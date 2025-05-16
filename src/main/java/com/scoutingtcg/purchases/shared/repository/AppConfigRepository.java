package com.scoutingtcg.purchases.shared.repository;

import com.scoutingtcg.purchases.shared.model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfig, String> {
}

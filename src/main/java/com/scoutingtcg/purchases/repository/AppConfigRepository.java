package com.scoutingtcg.purchases.repository;

import com.scoutingtcg.purchases.model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppConfigRepository extends JpaRepository<AppConfig, String> {
}

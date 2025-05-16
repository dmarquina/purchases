package com.scoutingtcg.purchases.shared.config;

import com.scoutingtcg.purchases.shared.model.AppConfig;
import com.scoutingtcg.purchases.shared.repository.AppConfigRepository;
import org.springframework.stereotype.Service;

@Service
public class AppConfigService {

    private final AppConfigRepository configRepo;

    public AppConfigService(AppConfigRepository configRepo) {
        this.configRepo = configRepo;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return configRepo.findById(key)
                .map(AppConfig::getValue)
                .map(Boolean::parseBoolean)
                .orElse(defaultValue);
    }

    public boolean isFreeShippingForCardsEnabled() {
        return getBoolean("free_shipping_cards", false);
    }

    public boolean isFreeShippingForProductsEnabled() {
        return getBoolean("free_shipping_products", false);
    }
}

package com.scoutingtcg.purchases.shared.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "app_config")
public class AppConfig {

    @Id
    @Column(name = "config_key")
    private String key;

    @Column(name = "config_value")
    private String value;

}

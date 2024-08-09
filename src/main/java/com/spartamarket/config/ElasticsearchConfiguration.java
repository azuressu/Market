package com.spartamarket.config;

import com.spartamarket.entity.ElasticsearchTimestamped;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.config.ElasticsearchConfigurationSupport;
import org.springframework.data.elasticsearch.core.event.BeforeConvertCallback;

@Configuration
public class ElasticsearchConfiguration extends ElasticsearchConfigurationSupport {

    @Bean
    public BeforeConvertCallback<ElasticsearchTimestamped> auditingListener() {
        return new ElasticsearchAuditingListener();
    }
}

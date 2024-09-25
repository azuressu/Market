package com.mymarket.config;


import com.mymarket.entity.ElasticsearchTimestamped;
import org.springframework.data.elasticsearch.core.event.BeforeConvertCallback;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
public class ElasticsearchAuditingListener implements BeforeConvertCallback<ElasticsearchTimestamped> {

    @Override
    public ElasticsearchTimestamped onBeforeConvert(ElasticsearchTimestamped entity, IndexCoordinates index) {
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        entity.setUpdatedAt(LocalDateTime.now());
        return entity;
    }

}
package com.spartamarket.repository;

import com.spartamarket.entity.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {

    List<ProductDocument> findByTitle(String title);

}

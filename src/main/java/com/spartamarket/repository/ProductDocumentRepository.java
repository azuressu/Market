package com.spartamarket.repository;

import com.spartamarket.entity.ProductDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductDocumentRepository extends ElasticsearchRepository<ProductDocument, String> {

    List<ProductDocument> findByTitle(String title);
    Page<ProductDocument> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
    Page<ProductDocument> findAll(Pageable pageable);

}

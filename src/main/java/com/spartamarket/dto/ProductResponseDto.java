package com.spartamarket.dto;

import com.spartamarket.entity.Product;
import com.spartamarket.entity.ProductDocument;
import jakarta.transaction.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

@Getter
@NoArgsConstructor
public class ProductResponseDto extends StatusResponseDto {

    private String title;
    private String content;
    private Integer price;
    private String username;
    private String createdAt;

    public ProductResponseDto(Product product) {
        this.title = product.getTitle();
        this.content = product.getContent();
        this.price = product.getPrice();
        this.username = product.getUser().getUsername();
        this.createdAt = String.valueOf(product.getCreatedAt());
    }

    public ProductResponseDto(ProductDocument productDocument) {
        this.title = productDocument.getTitle();
        this.content = productDocument.getContent();
        this.price = productDocument.getPrice();
        this.username = productDocument.getUsername();
        this.createdAt = String.valueOf(productDocument.getCreatedAt());
    }

}

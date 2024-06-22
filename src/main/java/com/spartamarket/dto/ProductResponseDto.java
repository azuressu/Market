package com.spartamarket.dto;

import com.spartamarket.entity.Product;
import com.spartamarket.entity.ProductDocument;
import jakarta.transaction.Status;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ProductResponseDto extends StatusResponseDto {

    private String title;
    private String content;
    private Integer price;
    private String username;
    private String createdAt;
    private String filepath;

    public ProductResponseDto(Product product) {
        this.title = product.getTitle();
        this.content = product.getContent();
        this.price = product.getPrice();
        this.username = product.getUser().getUsername();
        this.createdAt = product.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (product.getFilepath() != null) this.filepath = product.getFilepath();
    }

    public ProductResponseDto(ProductDocument productDocument) {
        this.title = productDocument.getTitle();
        this.content = productDocument.getContent();
        this.price = productDocument.getPrice();
        this.username = productDocument.getUsername();
        this.createdAt = productDocument.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}

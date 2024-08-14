package com.spartamarket.dto;

import com.spartamarket.entity.Product;
import com.spartamarket.entity.ProductDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ProductResponseDto extends StatusResponseDto {

    private String id;
    private String title;
    private String content;
    private Integer price;
    private String username;
    private String createdAt;

    public ProductResponseDto(Product product) {
        this.id = String.valueOf(product.getId());
        this.title = product.getTitle();
        this.content = product.getContent().length() <= 15 ? product.getContent() : product.getContent().substring(0, 15);
        this.price = product.getPrice();
        this.username = product.getUser().getUsername();
        this.createdAt = product.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public ProductResponseDto(ProductDocument productDocument) {
        this.id = productDocument.getId();
        this.title = productDocument.getTitle();
        this.content = productDocument.getContent().length() <= 15 ? productDocument.getContent() : productDocument.getContent().substring(0, 15);
        this.price = productDocument.getPrice();
        this.username = productDocument.getUser().getUsername();
        this.createdAt = productDocument.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}

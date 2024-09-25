package com.mymarket.dto;

import com.mymarket.entity.Product;
import com.mymarket.entity.ProductDocument;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor
public class ProductsResponseDto extends StatusResponseDto{

    private String id;
    private String title;
    private String content;
    private Integer price;
    private String username;
    private String createdAt;

    public ProductsResponseDto(Product product) {
        this.id = String.valueOf(product.getId());
        this.title = product.getTitle();
        this.content = product.getContent().replace("\n", "<br>");
        this.price = product.getPrice();
        this.username = product.getUser().getUsername();
        this.createdAt = product.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public ProductsResponseDto(Product product, String update) {
        this.id = String.valueOf(product.getId());
        this.title = product.getTitle();
        this.content = product.getContent();
        this.price = product.getPrice();
    }

    public ProductsResponseDto(ProductDocument productDocument) {
        this.id = productDocument.getId();
        this.title = productDocument.getTitle();
        this.content = productDocument.getContent().replace("\n", "<br>");
        this.price = productDocument.getPrice();
        this.username = productDocument.getUser().getUsername();
        this.createdAt = productDocument.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public ProductsResponseDto(ProductDocument productDocument, String update) {
        this.id = productDocument.getId();
        this.title = productDocument.getTitle();
        this.content = productDocument.getContent();
        this.price = productDocument.getPrice();
    }
}

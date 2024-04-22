package com.spartamarket.dto;

import com.spartamarket.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductResponseDto {

    private String title;
    private String content;
    private Integer price;
    private String username;

    public ProductResponseDto(Product product) {
        this.title = product.getTitle();
        this.content = product.getContent();
        this.price = product.getPrice();
        this.username = product.getUser().getUsername();
    }


}

package com.spartamarket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductRequestDto {

    private String title;
    private String content;
    private Integer price;
}

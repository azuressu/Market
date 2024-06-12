package com.spartamarket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDto {

    private String title;
    private String content;
    private Integer price;
}

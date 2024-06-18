package com.spartamarket.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class ProductRequestDto {

    private String title;
    private String content;
    private Integer price;
    private MultipartFile file;
}

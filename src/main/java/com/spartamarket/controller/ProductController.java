package com.spartamarket.controller;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductResponseDto;
import com.spartamarket.dto.StatusResponseDto;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDto>> getProductList() {
        List<ProductResponseDto> productResponseDtos = productService.getProductList();
        return ResponseEntity.ok().body(productResponseDtos);
    }

    @GetMapping("/products/productId")
    public ResponseEntity<StatusResponseDto> getOneProduct(@RequestParam Long productId) {
        ProductResponseDto productResponseDto;
        try {
            productResponseDto = productService.getOneProduct(productId);
        } catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok().body(productResponseDto);
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponseDto>> getSearchPosts(@RequestParam String title) {
        log.info("입력받은 검색어: " + title);
        List<ProductResponseDto> productResponseDtos = productService.getSearchPosts(title);
        return ResponseEntity.ok().body(productResponseDtos);
    }

    @PostMapping("/products")
    public ResponseEntity<StatusResponseDto> createProduct(ProductRequestDto productRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(userDetails.getUsername());
        String returnStatus;
        try {
            returnStatus = productService.createProduct(productRequestDto, userDetails.getUsername());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(201).body(new StatusResponseDto(returnStatus, HttpStatus.CREATED.value()));
    }

    @PutMapping("/products/productId")
    public ResponseEntity<StatusResponseDto> updateProduct(@RequestBody ProductRequestDto productRequestDto,
                                                           @RequestParam Long productId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnStatus;
        try {
            returnStatus = productService.updateProduct(productRequestDto, productId, userDetails);
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new StatusResponseDto(returnStatus, HttpStatus.OK.value()));
    }

    @DeleteMapping("/products/productId")
    public ResponseEntity<StatusResponseDto> deleteProduct(@RequestParam Long productId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnStatus;
        try {
            returnStatus = productService.deleteProduct(productId, userDetails);
        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new StatusResponseDto(returnStatus, HttpStatus.OK.value()));
    }

    // 물품 전체 삭제
    @DeleteMapping("/products")
    public ResponseEntity<StatusResponseDto> deleteProductDocument() {
        productService.deleteProductDocument();
        return ResponseEntity.ok().body(new StatusResponseDto("삭제 완료", HttpStatus.OK.value()));
    }

}

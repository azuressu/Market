package com.spartamarket.controller;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductResponseDto;
import com.spartamarket.dto.StatusResponseDto;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
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
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok().body(productResponseDto);
    }

    @PostMapping("/products")
    public ResponseEntity<StatusResponseDto> createProduct(@RequestBody ProductRequestDto productRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnStatus;
        try {
            returnStatus = productService.createProduct(productRequestDto, userDetails);
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

}

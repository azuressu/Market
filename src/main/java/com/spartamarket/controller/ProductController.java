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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // 게시글 전체 조회
    @GetMapping("/products")
    public String getProductList(Model model) {
        List<ProductResponseDto> productResponseDtos = productService.getProductList();
        model.addAttribute("products", productResponseDtos);
        return "products";
    }

    // 게시글 단건 조회
    @GetMapping("/products/{productId}")
    public String getOneProduct(@PathVariable Long productId, Model model,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("ID: " + productId.toString());
        ProductResponseDto productResponseDto = null;
        try {
            productResponseDto = productService.getOneProduct(productId);
            model.addAttribute("product", productResponseDto);
            Boolean isUser = productService.isUser(userDetails, productResponseDto.getUsername());
            model.addAttribute("isUser", isUser);
        } catch (NoSuchElementException e) {
            ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return "product";
    }

    // 게시글 제목 검색
    @GetMapping("/products/search")
    public ResponseEntity<List<ProductResponseDto>> getSearchProducts(@RequestParam String title) {
        log.info("입력받은 검색어: " + title);
        List<ProductResponseDto> productResponseDtos = productService.getSearchPosts(title);
        return ResponseEntity.ok().body(productResponseDtos);
    }

    // 게시글 생성
    @PostMapping("/products")
    public ResponseEntity<StatusResponseDto> createProduct(ProductRequestDto productRequestDto,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(userDetails.getUsername());
        String returnURL;
        try {
            returnURL = productService.createProduct(productRequestDto, userDetails.getUsername());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(201).body(new StatusResponseDto(returnURL, HttpStatus.CREATED.value()));
    }

    // 게시글 수정 페이지
    @GetMapping("/uproducts")
    public String updateProductPage(@RequestParam Long productId,
                                    @AuthenticationPrincipal UserDetailsImpl userDetails,
                                    Model model) {
        Boolean isUser = productService.isUser(userDetails, productId);
        if (isUser) {
            ProductResponseDto productResponseDto = productService.getOneProduct(productId);
            model.addAttribute("product", productResponseDto);
            return "updateproduct";

        } else {
            return "redirect:/api/products/" + productId;
        }
    }

    // 게시글 수정
    @PutMapping("/products/{productId}")
    public ResponseEntity<StatusResponseDto> updateProduct(ProductRequestDto productRequestDto,
                                                           @PathVariable Long productId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("productId = " + productId);
        String returnStatus;
        try {
            returnStatus = productService.updateProduct(productRequestDto, productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new StatusResponseDto(returnStatus, HttpStatus.OK.value()));
    }

    // 게시글 삭제
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<StatusResponseDto> deleteProduct(@PathVariable Long productId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnStatus;
        try {
            returnStatus = productService.deleteProduct(productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new StatusResponseDto(returnStatus, HttpStatus.OK.value()));
    }

}

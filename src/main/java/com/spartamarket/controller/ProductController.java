package com.spartamarket.controller;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductResponseDto;
import com.spartamarket.dto.ProductsResponseDto;
import com.spartamarket.dto.StatusResponseDto;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
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
    public String getProductList(Model model, @RequestParam(value="page", defaultValue = "0") Integer page) {
        Page<ProductResponseDto> productResponseDtos = productService.getProductList(page);
        model.addAttribute("products", productResponseDtos);
        return "products";
    }

    // 게시글 단건 조회
    @GetMapping("/products/{productId}")
    public String getOneProduct(@PathVariable Long productId, Model model,
                                @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("ID: " + productId.toString());
        ProductsResponseDto productResponseDto = null;
        try {
            productResponseDto= productService.getOneProduct(productId);
            model.addAttribute("product", productResponseDto);
            Boolean isUser = productService.isUser(userDetails, productResponseDto.getUsername());
            model.addAttribute("isUser", isUser);
        } catch (NoSuchElementException e) {
            ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return "product";
    }

    // 게시글 제목 및 내용 검색
    @GetMapping("/products/search")
    public String getSearchProductsPage(@RequestParam String search) {
        log.info("입력받은 검색어: " + search);
        return "searchproduct";
    }

    // 게시글 slice해서 내용 반환
    @GetMapping("/products/searching")
    @ResponseBody
    public Slice<ProductResponseDto> getSearchProducts(@RequestParam String search,
                                    @RequestParam(required = false, defaultValue = "0") Integer page) {
        log.info("입력받은 검색어: " + search);
        log.info("요쳥받은 페이지: " + page);
        Slice<ProductResponseDto> productResponseDtos = productService.getSearchPosts(search, page);
        return productResponseDtos;
    }

    // 게시글 생성 or 수정
    @GetMapping("/product")
    public String addproductPage(@RequestParam(required = false) Long productId,
                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                 Model model) {
        if (userDetails == null) return "redirect:/api/products";
        log.info(userDetails.getUsername());
        if (productId == null) {
            model.addAttribute("product", new ProductResponseDto());
        } else {
            log.info(productId.toString());
            Boolean isUser = productService.isUser(userDetails, productId);
            if (isUser) {
                ProductsResponseDto productResponseDto = productService.getUpdateOneProduct(productId);
                model.addAttribute("product", productResponseDto);
            } else {
                return "redirect:/api/products/" + productId;
            }
        }
        return "addproduct";
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
        log.info(productId.toString());
        String returnStatus;
        try {
            returnStatus = productService.deleteProduct(productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok().body(new StatusResponseDto(returnStatus, HttpStatus.OK.value()));
    }

}

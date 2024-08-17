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

import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * 게시글 전체 조회 페이지
     * 게시글 전체 내용을 조회해서 담아 페이지를 반환하는 메서드
     * @param model : 게시글들 내용을 담을 Model
     * @param page : 게시글 페이지 수
     * @return : products.html 페이지 리턴
     */
    @GetMapping("/products")
    public String getProductList(Model model, @RequestParam(value="page", defaultValue = "0") Integer page) {
        Page<ProductResponseDto> productResponseDtos = productService.getProductList(page);
        model.addAttribute("products", productResponseDtos);
        return "products";
    }

    /**
     * 게시글 단건 조회 페이지
     * 게시글 단건 조회하는 메서드
     * @param productId : 조회할 게시글의 ID
     * @param model : 게시글 내용을 담을 Model
     * @param userDetails : 사용자에 대한 정보
     * @return : product.html 페이지 리턴
     */
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

    /**
     * 게시글 검색 페이지
     * 제목 및 내용으로 게시글을 검색하는 메서드
     * @param search : 제목 및 내용에 대한 검색어
     * @return : searchproduct.html 페이지 리턴
     */
    @GetMapping("/products/search")
    public String getSearchProductsPage(@RequestParam String search) {
        log.info("입력받은 검색어: " + search);
        return "searchproduct";
    }

    /**
     * 검색어에 대한 게시글 내용 반환
     * 검색어에 대한 게시글을 반환하는 메서드
     * @param search : 제목 및 내용에 대한 검색어
     * @param page : 검색어에 대한 게시글 페이지 수
     * @return : 검색어에 대한 게시글 내용 반환
     */
    @GetMapping("/products/searching")
    @ResponseBody
    public Slice<ProductResponseDto> getSearchProducts(@RequestParam String search,
                                    @RequestParam(required = false, defaultValue = "0") Integer page) {
        log.info("입력받은 검색어: " + search);
        log.info("요쳥받은 페이지: " + page);
        Slice<ProductResponseDto> productResponseDtos = productService.getSearchPosts(search, page);
        return productResponseDtos;
    }

    /**
     * 게시글 생성 OR 수정 페이지
     * 게시글 생성 OR 수정 페이지를 반환하는 메서드
     * @param productId : 수정할 게시글의 게시글 ID
     * @param userDetails : 사용자에 대한 정보
     * @param model : 게시글에 대한 정보를 담을 Model
     * @return : addproduct.html 페이지 리턴
     */
    @GetMapping("/product")
    public String addProductPage(@RequestParam(required = false) Long productId,
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

    /**
     * 게시글 생성
     * 게시글 생성하는 메서드
     * @param productRequestDto : 생성할 게시글 내용 (title, content, price)
     * @param userDetails : 사용자에 대한 정보
     * @return : ResponseEntity에 반환 URL과 상태코드를 담아 리턴
     */
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

    /**
     * 게시글 수정
     * @param productRequestDto : 수정할 게시글 내용 (title, content, price)
     * @param productId : 수정할 게시글의 ID
     * @param userDetails : 사용자에 대한 정보
     * @return : ResponseEntity에 반환 URL과 상태코드를 담아 리턴
     */
    @PutMapping("/products/{productId}")
    public ResponseEntity<StatusResponseDto> updateProduct(ProductRequestDto productRequestDto,
                                                           @PathVariable Long productId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("productId = " + productId);
        String returnURL;
        try {
            returnURL = productService.updateProduct(productRequestDto, productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new StatusResponseDto(returnURL, HttpStatus.OK.value()));
    }

    /**
     * 게시글 삭제
     * @param productId : 삭제할 게시글의 ID
     * @param userDetails : 사용자에 대한 정보
     * @return : ResponseEntity에 반환 URL과 상태코드를 담아 리턴
     */
    @DeleteMapping("/products/{productId}")
    public ResponseEntity<StatusResponseDto> deleteProduct(@PathVariable Long productId,
                                                           @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(productId.toString());
        String returnURL;
        try {
            returnURL = productService.deleteProduct(productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok().body(new StatusResponseDto(returnURL, HttpStatus.OK.value()));
    }

}

package com.spartamarket.service;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductResponseDto;
import com.spartamarket.dto.ProductsResponseDto;
import com.spartamarket.entity.Product;
import com.spartamarket.entity.User;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.repository.ProductRepository;
import com.spartamarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 게시글 전체 조회
    public Page<ProductResponseDto> getProductList(Integer page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Product> productList = productRepository.findAll(pageable);

        return productList.map(ProductResponseDto::new);
    }

    // 게시글 검색
    public Slice<ProductResponseDto> getSearchPosts(String search, Integer page) {
        log.info("Service 넘어온 검색어: " + search);

        Pageable pageable = PageRequest.of(page, 5);
        // JPA 검색
        Slice<Product> searchProducts = productRepository.findByTitleContainingOrContentContaining(search, search, pageable);
        return searchProducts.map(ProductResponseDto::new);
    }

    // 게시글 단건 조회
    public ProductsResponseDto getOneProduct(Long productId) {
        Product product = findProduct(productId);
        ProductsResponseDto productResponseDto = new ProductsResponseDto(product);
        log.info("title : " + productResponseDto.getTitle());
        log.info("content : " + productResponseDto.getContent());
        return productResponseDto;
    }

    // 게시글 작성
    public String createProduct(ProductRequestDto productRequestDto, String username) {
        User user = findUser(username);

        Product product = new Product(productRequestDto, user);

        productRepository.save(product);
        return "/api/products/" + product.getId();
    }

    // 게시글 수정 글 조회
    public ProductsResponseDto getUpdateOneProduct(Long productId) {
        Product product = findProduct(productId);
        ProductsResponseDto productResponseDto = new ProductsResponseDto(product, "update");
        return productResponseDto;
    }

    // 게시글 수정
    @Transactional
    public String updateProduct(ProductRequestDto productRequestDto, Long productId, UserDetailsImpl userDetails) {
        User user = findUser(userDetails.getUsername());
        Product product = findProduct(productId);

        if (!user.getUsername().equals(product.getUser().getUsername())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        product.updateProduct(productRequestDto);

        return "/api/products/" + product.getId();
    }

    // 게시글 삭제
    public String deleteProduct(Long productId, UserDetailsImpl userDetails) {
        User user = findUser(userDetails.getUsername());
        Product product = findProduct(productId);

        if (!user.getUsername().equals(product.getUser().getUsername())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        productRepository.delete(product);
        return "/api/products/";
    }

    // 사용자 찾기
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));
    }

    // 물품 찾기
    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 물품"));
    }

    public Boolean isUser(UserDetailsImpl userDetails, String username) {
        if (userDetails == null) {
            return false;
        }
        if (!userDetails.getUsername().equals(username)) {
            return false;
        }
        return true;
    }

    public Boolean isUser(UserDetailsImpl userDetails, Long productId) {
        if (userDetails == null) {
            return false;
        }

        Product product = findProduct(productId);
        String username = product.getUser().getUsername();
        if (!userDetails.getUsername().equals(username)) {
            return false;
        }
        return true;
    }
}

package com.spartamarket.service;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductResponseDto;
import com.spartamarket.entity.Product;
import com.spartamarket.entity.ProductDocument;
import com.spartamarket.entity.User;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.repository.ProductDocumentRepository;
import com.spartamarket.repository.ProductRepository;
import com.spartamarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductDocumentRepository productDocumentRepository;
    private final UserRepository userRepository;

    // 게시글들 조회
    public List<ProductResponseDto> getProductList() {
//        List<Product> productList = productRepository.findAll();
        List<ProductDocument> productList = StreamSupport
                .stream(productDocumentRepository.findAll().spliterator(), false)
                .toList();

        /*StreamSupport
  .stream(iterable.spliterator(), false)
  .collect(Collectors.toList());*/
        List<ProductResponseDto> productResponseDtos = new ArrayList<>();

        for (ProductDocument product : productList) {
            productResponseDtos.add(new ProductResponseDto(product));
        }

        return productResponseDtos;
    }

    // 게시글 검색
    public List<ProductResponseDto> getSearchPosts(String title) {
        log.info("Service 넘어온 검색어: " + title);

        // ElasticSearch 검색
        List<ProductDocument> searchHits = productDocumentRepository.findByTitle(title);

        // 빈 ArrayList 생성
        ArrayList<ProductResponseDto> products = new ArrayList<>();

        // 결과 내용 ProductResponseDto에 담기
        for (ProductDocument p : searchHits) {
            ProductResponseDto productResponseDto = new ProductResponseDto(p);
            products.add(productResponseDto);
        }

        return products;
    }

    // 게시글 단건 조회
    public ProductResponseDto getOneProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글"));

        return new ProductResponseDto(product);
    }

    // 게시글 작성
    public String createProduct(ProductRequestDto productRequestDto, UserDetailsImpl userDetails) {
        User user = findUser(userDetails.getUsername());

        Product product = new Product(productRequestDto, user);

        productRepository.save(product);

        ProductDocument productDocument = new ProductDocument(productRequestDto, user, product.getCreatedAt(), product.getUpdatedAt());
        productDocumentRepository.save(productDocument);
        return "물품 등록 성공";
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

        return "게시글 수정 성공";
    }

    // 게시글 삭제
    public String deleteProduct(Long productId, UserDetailsImpl userDetails) {
        User user = findUser(userDetails.getUsername());
        Product product = findProduct(productId);

        if (!user.getUsername().equals(product.getUser().getUsername())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        return "게시글 삭제 성공";
    }

    // 게시글 전체 삭제
    public void deleteProductDocument() {
        productDocumentRepository.deleteAll();
    }

    // 사용자 찾기
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));
    }

    // 물품 찾기
    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException());
    }
}

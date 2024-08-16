package com.spartamarket.service;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductResponseDto;
import com.spartamarket.dto.ProductsResponseDto;
import com.spartamarket.entity.ProductDocument;
import com.spartamarket.entity.User;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.repository.ProductDocumentRepository;
import com.spartamarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDocumentService {

    private final ProductDocumentRepository productDocumentRepository;
    private final UserRepository userRepository;

    // 게시글 전체 조회
    public Page<ProductResponseDto> getProductDocumentList(Integer page) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<ProductDocument> productDocumentPage = productDocumentRepository.findAll(pageable);

        return productDocumentPage.map(ProductResponseDto::new);
    }

    // 게시글 작성
    public String createProductDocument(ProductRequestDto productRequestDto, String username) {
        User user = findUser(username);

        ProductDocument productDocument = new ProductDocument(productRequestDto, user);

        productDocumentRepository.save(productDocument);
        log.info(productDocument.getUser().toString());
        log.info(productDocument.getUser().getUsername());
        return "/api/productdocuments/" + productDocument.getId();
    }

    // 게시글 검색
    public Slice<ProductResponseDto> getSearchProductDocuments(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 6);
        // 검색
        Page<ProductDocument> searchProductDocuments = productDocumentRepository.findByTitleContainingOrContentContaining(search, search, pageable);

        List<ProductResponseDto> searchPD = searchProductDocuments.getContent().stream().map(ProductResponseDto::new).collect(Collectors.toList());

        return new SliceImpl<>(searchPD, pageable, searchProductDocuments.hasNext());
    }

    // 게시글 단건 조회
    public ProductsResponseDto getOneProductDocument(String productId) {
        ProductDocument productDocument = findProductDocument(productId);
        log.info(productDocument.getTitle());
        return new ProductsResponseDto(productDocument);
    }

    // 게시글 수정 내용 불러오기
    public ProductsResponseDto getUpdateOneProductDocument(String productId) {
        ProductDocument productDocument = findProductDocument(productId);
        log.info(productDocument.getTitle());
        return new ProductsResponseDto(productDocument, "update");
    }

    // 게시글 수정
    @Transactional
    public String updateProductDocument(ProductRequestDto productRequestDto, String productId, UserDetailsImpl userDetails) {
        User user = findUser(userDetails.getUsername());
        ProductDocument productDocument = findProductDocument(productId);

        if (!user.getId().equals(productDocument.getUser().getId())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        productDocument.updateProductDocument(productRequestDto);
        productDocumentRepository.save(productDocument);
        return "/api/productdocuments/" + productId;
    }

    // 게시글 삭제
    public String deleteProductDocument(String productId, UserDetailsImpl userDetails){
        User user = findUser(userDetails.getUsername());
        ProductDocument productDocument = findProductDocument(productId);

        if (!user.getId().equals(productDocument.getUser().getId())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        productDocumentRepository.delete(productDocument);
        return "/api/productdocuments";
    }

    // 게시글 전체 삭제
    public void deleteAllProductDocument() {
        productDocumentRepository.deleteAll();
    }

    public Boolean isUser(UserDetailsImpl userDetails, String productId) {
        ProductDocument productDocument = findProductDocument(productId);
        if (userDetails == null) { return false; }

        if (productDocument.getUser().getId().equals(userDetails.getUser().getId())) {
            return true;
        }
        return false;
    }

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));
    }

    private ProductDocument findProductDocument(String productId) {
        return productDocumentRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글"));
    }


}

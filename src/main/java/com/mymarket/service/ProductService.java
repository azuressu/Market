package com.mymarket.service;

import com.mymarket.dto.ProductRequestDto;
import com.mymarket.dto.ProductResponseDto;
import com.mymarket.dto.ProductsResponseDto;
import com.mymarket.entity.Product;
import com.mymarket.entity.User;
import com.mymarket.jwt.UserDetailsImpl;
import com.mymarket.repository.ProductRepository;
import com.mymarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 전체 조회
     * 게시글 전체를 조회하는 메서드
     * @param page : 조회할 전체 게시글의 페이지 수
     * @return : 페이지에 해당하는 게시글들의 내용 반환
     */
    public Page<ProductResponseDto> getProductList(Integer page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Product> productList = productRepository.findAll(pageable);

        return productList.map(ProductResponseDto::new);
    }

    /**
     * 게시글 검색
     * 제목 및 내용으로 게시글을 검색하는 메서드
     * @param search : 제목 및 내용에 대한 검색어
     * @param page : 검색어에 대한 게시글 페이지 수
     * @return : 검색어에 대한, 해당 페이지에 해당하는 게시글들의 내용 반환
     */
    public Slice<ProductResponseDto> getSearchPosts(String search, Integer page) {
        log.info("Service 넘어온 검색어: " + search);

        Pageable pageable = PageRequest.of(page, 6);
        // JPA 검색
        Slice<Product> searchProducts = productRepository.findByTitleContainingOrContentContaining(search, search, pageable);
        return searchProducts.map(ProductResponseDto::new);
    }

    /**
     * 게시글 단건 조회
     * 게시글을 단건 조회하는 메서드
     * @param productId : 조회할 게시글의 번호
     * @return : 게시글 번호헤 해당하는 게시글의 내용 반환
     */
    public ProductsResponseDto getOneProduct(Long productId) {
        Product product = findProduct(productId);
        ProductsResponseDto productResponseDto = new ProductsResponseDto(product);
        log.info("title : " + productResponseDto.getTitle());
        log.info("content : " + productResponseDto.getContent());
        return productResponseDto;
    }

    /**
     * 게시글 작성
     * 게시글을 작성하는 메서드
     * @param productRequestDto : 작성할 게시글의 내용 (title, content, price)
     * @param username : 현재 로그인한 사용자의 username
     * @return : 새롭게 생성된 게시글에 해당하는 페이지 URL 반환
     */
    public String createProduct(ProductRequestDto productRequestDto, String username) {
        User user = findUser(username);

        Product product = new Product(productRequestDto, user);

        productRepository.save(product);
        return "/api/products/" + product.getId();
    }

    /**
     * 수정할 게시글 내용
     * 수정할 게시글의 내용만을 반환하는 메서드
     * @param productId : 수정할 게시글의 게시글 번호
     * @return : 수정할 게시글의 내용 반환
     */
    public ProductsResponseDto getUpdateOneProduct(Long productId) {
        Product product = findProduct(productId);
        ProductsResponseDto productResponseDto = new ProductsResponseDto(product, "update");
        return productResponseDto;
    }

    /**
     * 게시글 수정
     * @param productRequestDto : 수정할 게시글의 내용 (title, content, price)
     * @param productId : 수정할 게시글의 게시글 번호
     * @param userDetails : 현재 로그인한 사용자의 정보
     * @return : 수정을 완료한 게시글의 URL 반환
     */
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

    /**
     * 게시글 삭제
     * 게시글을 삭제하는 메서드
     * @param productId : 삭제할 게시글의 번호
     * @param userDetails : 현재 로그인한 사용자의 정보
     * @return : 전체 게시글 페이지 URL 리턴
     */
    public String deleteProduct(Long productId, UserDetailsImpl userDetails) {
        User user = findUser(userDetails.getUsername());
        Product product = findProduct(productId);

        if (!user.getUsername().equals(product.getUser().getUsername())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        productRepository.delete(product);
        return "/api/products";
    }

    /**
     * 사용자 찾기
     * username으로 사용자를 찾는 메서드
     * @param username : 현재 사용자의 username
     * @return : 찾은 사용자를 리턴 (없다면 UsernameNotFoundException Throw)
     */
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));
    }

    /**
     * 게시글 찾기
     * productId로 게시글을 찾는 메서드
     * @param productId : 찾으려는 게시글의 ID
     * @return : 찾은 게시글을 리턴 (없다면 NoSuchElementException Throw)
     */
    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 물품"));
    }

    /**
     * 로그인한 사용자와 요청한 작업이 가능한 사용자가 일치하는지
     * 로그인한 사용자와, 요청에 해당하는 내용을 실행할 수 있는 사용자가 일치하는지 확인하는 메서드
     * @param userDetails : 현재 로그인한 사용자에 대한 정보
     * @param username : 작업을 요청한 사용자의 username
     * @return : 맞다면 true, 맞지 않다면 false 반환
     */
    public Boolean isUser(UserDetailsImpl userDetails, String username) {
        if (userDetails == null) {
            return false;
        }
        if (!userDetails.getUsername().equals(username)) {
            return false;
        }
        return true;
    }

    /**
     * 로그인한 사용자와 요청한 작업이 가능한 사용자가 일치하는지
     * 로그인한 사용자와, 작업을 요청이 가능한 사용자가 일치하는지 확인하는 메서드
     * @param userDetails : 현재 로그인한 사용자에 대한 정보
     * @param productId : 작업을 요청받은 게시글의 ID
     * @return : 맞다면 true, 맞지 않다면 false 반환
     */
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

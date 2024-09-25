package com.mymarket.service;

import com.mymarket.dto.ProductRequestDto;
import com.mymarket.dto.ProductResponseDto;
import com.mymarket.dto.ProductsResponseDto;
import com.mymarket.entity.ProductDocument;
import com.mymarket.entity.User;
import com.mymarket.jwt.UserDetailsImpl;
import com.mymarket.repository.ProductDocumentRepository;
import com.mymarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductDocumentService {

    private final ProductDocumentRepository productDocumentRepository;
    private final UserRepository userRepository;

    /**
     * 게시글 전체 조회
     * 게시글 전체를 조회하는 메서드
     * @param page : 조회할 전체 게시글의 페이지 수
     * @return : 페이지에 해당하는 게시글들의 내용을 반환
     */
    public Page<ProductResponseDto> getProductDocumentList(Integer page) {

        Pageable pageable = PageRequest.of(page, 5);
        Page<ProductDocument> productDocumentPage = productDocumentRepository.findAll(pageable);

        return productDocumentPage.map(ProductResponseDto::new);
    }

    /**
     * 게시글 작성
     * 게시글을 작성(생성)하는 메서드
     * @param productRequestDto : 작성할 게시글의 내용 (title, content, price)
     * @param username : 현재 요청한 사용자의 username
     * @return : 작성된 게시글의 페이지 URL 반환
     */
    public String createProductDocument(ProductRequestDto productRequestDto, String username) {
        User user = findUser(username);

        ProductDocument productDocument = new ProductDocument(productRequestDto, user);

        productDocumentRepository.save(productDocument);
        log.info(productDocument.getUser().toString());
        log.info(productDocument.getUser().getUsername());
        return "/api/productdocuments/" + productDocument.getId();
    }

    /**
     * 게시글 검색
     * 제목 및 내용으로 게시글을 검색하는 메서드
     * @param search : 제목 및 내용에 대한 검색어
     * @param page : 검색어에 대한 게시글의 페이지 수
     * @return : 검색어에 대한, 해당 페이지의 게시글들의 내용 반환
     */
    public Slice<ProductResponseDto> getSearchProductDocuments(String search, Integer page) {
        Pageable pageable = PageRequest.of(page, 6);
        // 검색
        Page<ProductDocument> searchProductDocuments = productDocumentRepository.findByTitleContainingOrContentContaining(search, search, pageable);

        List<ProductResponseDto> searchPD = searchProductDocuments.getContent().stream().map(ProductResponseDto::new).collect(Collectors.toList());

        return new SliceImpl<>(searchPD, pageable, searchProductDocuments.hasNext());
    }

    /**
     * 게시글 단건 조회
     * 게시글을 단건 조회하는 메서드
     * @param productId : 조회할 게시글의 ID
     * @return : ID에 해당하는 게시글 내용 반환
     */
    public ProductsResponseDto getOneProductDocument(String productId) {
        ProductDocument productDocument = findProductDocument(productId);
        log.info(productDocument.getTitle());
        return new ProductsResponseDto(productDocument);
    }

    /**
     * 수정할 게시글 내용
     * 수정할 게시글의 내용 불러오기
     * @param productId : 수정할 게시글의 ID
     * @return : 수정할 ID에 맞는 게시글의 내용 반환
     */
    public ProductsResponseDto getUpdateOneProductDocument(String productId) {
        ProductDocument productDocument = findProductDocument(productId);
        log.info(productDocument.getTitle());
        return new ProductsResponseDto(productDocument, "update");
    }

    /**
     * 게시글 수정
     * @param productRequestDto : 수정할 게시글의 내용 (title, content, price)
     * @param productId : 수정할 게시글의 ID
     * @param userDetails : 현재 로그인한 사용자의 정보
     * @return : 수정을 완료한 게시글의 페이지 URL 반환
     */
    @Transactional
    public String updateProductDocument(ProductRequestDto productRequestDto, String productId, UserDetailsImpl userDetails) {
        User user = findUser(userDetails.getUsername());
        ProductDocument productDocument = findProductDocument(productId);

        if (!user.getUsername().equals(productDocument.getUser().getUsername())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        productDocument.updateProductDocument(productRequestDto);
        productDocumentRepository.save(productDocument);
        return "/api/productdocuments/" + productId;
    }

    /**
     * 게시글 삭제
     * 게시글을 삭제하는 메서드
     * @param productId : 삭제할 게시글의 ID
     * @param userDetails : 현재 로그인한 사용자의 정보
     * @return : 게시글들 전체 페이지 URL 반환
     */
    public String deleteProductDocument(String productId, UserDetailsImpl userDetails){
        User user = findUser(userDetails.getUsername());
        ProductDocument productDocument = findProductDocument(productId);

        if (!user.getUsername().equals(productDocument.getUser().getUsername())) {
            throw new IllegalArgumentException("글 작성자가 아닙니다");
        }

        productDocumentRepository.delete(productDocument);
        return "/api/productdocuments";
    }

    /**
     * 로그인한 사용자와 요청한 작업이 가능한 사용자가 일치하는가
     * 로그인한 사용자와 요청한 작업이 실제 가능한 사용자가 일치하는지 확인하는 메서드
     * @param userDetails : 현재 로그인한 사용자에 대한 정보
     * @param productId : 작업을 요청받은 게시글의 ID
     * @return : 일치하면 true, 일치하지 않는다면 false 반환
     */
    public Boolean isUser(UserDetailsImpl userDetails, String productId) {
        ProductDocument productDocument = findProductDocument(productId);
        if (userDetails == null) { return false; }

        if (productDocument.getUser().getId().equals(userDetails.getUser().getId())) {
            return true;
        }
        return false;
    }

    /**
     * 사용자 찾기
     * 사용자를 찾는 메서드
     * @param username : 찾을 사용자의 username
     * @return : username으로 찾아낸 사용자 반환(없다면 UsernameNotFoundException Throw)
     */
    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));
    }

    /**
     * 게시글 찾기
     * 게시글을 찾는 메서드
     * @param productId : 찾을 게시글의 ID
     * @return : ID로 찾아낸 게시글 반환(없다면 NoSuchElementException Throw)
     */
    private ProductDocument findProductDocument(String productId) {
        return productDocumentRepository.findById(productId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글"));
    }


}

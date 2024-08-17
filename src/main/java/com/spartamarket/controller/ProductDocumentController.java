package com.spartamarket.controller;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductResponseDto;
import com.spartamarket.dto.ProductsResponseDto;
import com.spartamarket.dto.StatusResponseDto;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.service.ProductDocumentService;
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
public class ProductDocumentController {

    private final ProductDocumentService productDocumentService;

    /**
     * 게시글 전체 조회 페이지
     * 게시글 전체 내용을 조회해서 담아 페이지를 반환하는 메서드
     * @param model : 게시글들 내용을 담을 Model
     * @param page : 게시글 페이지 수
     * @return : productdocuments.html 페이지 리턴
     */
    @GetMapping("/productdocuments")
    public String getProductDocumentList(Model model, @RequestParam(value="page", defaultValue = "0") Integer page) {
        Page<ProductResponseDto> productResponseDtos = productDocumentService.getProductDocumentList(page);
        model.addAttribute("productdocuments", productResponseDtos);
        return "productdocuments";
    }

    /**
     * 게시글 단건 조회 페이지
     * 게시글 단건 조회하는 메서드
     * @param productId : 조회할 게시글의 ID
     * @param model : 게시글 내용을 담을 Model
     * @param userDetails : 사용자에 대한 정보
     * @return : productdocument.html 페이지 리턴
     */
    @GetMapping("/productdocuments/{productId}")
    public String getOneProductDocument(@PathVariable String productId, Model model,
                                        @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(productId);
        Boolean isUser = productDocumentService.isUser(userDetails, productId);
        log.info("사용자인지 아닌지: " + isUser);
        try {
            ProductsResponseDto productResponseDto = productDocumentService.getOneProductDocument(productId);
            model.addAttribute("isUser", isUser);
            model.addAttribute("productdocument", productResponseDto);
        } catch (NoSuchElementException e) {
            ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return "productdocument";
    }

    /**
     * 게시글 생성 OR 수정 페이지
     * 게시글 생성 OR 수정 페이지를 반환하는 메서드
     * @param productId : 수정할 게시글의 게시글 ID
     * @param userDetails : 사용자에 대한 정보
     * @param model : 게시글에 대한 정보를 담을 Model
     * @return : addproductdocument.html 페이지 리턴
     */
    @GetMapping("/productdocument")
    public String addProductDocumentPage(@RequestParam(required=false) String productId,
                                         @AuthenticationPrincipal UserDetailsImpl userDetails,
                                         Model model) {
        if (userDetails == null) return "redirect:/api/productdocuments";

        log.info(userDetails.getUsername());
        if (productId == null) {
            model.addAttribute("product", new ProductResponseDto());
        } else {
            log.info(productId);
            Boolean isUser = productDocumentService.isUser(userDetails, productId);
            if (isUser) {
                ProductsResponseDto productsResponseDto = productDocumentService.getUpdateOneProductDocument(productId);
                model.addAttribute("product", productsResponseDto);
            } else {
                return "redirect:/api/productdocuments/" + productId;
            }
        }
        return "addproductdocument";
    }

    // 게시글 검색 페이지

    /**
     * 게시글 검색 페이지
     * 제목 및 내용으로 게시글을 검색하는 메서드
     * @param search : 제목 및 내용에 대한 검색어
     * @return : searchproductdocument.html 페이지 리턴
     */
    @GetMapping("/productdocuments/search")
    public String getSearchProductDocumentsPage(@RequestParam String search) {
        log.info("입력받은 검색어 : " + search);
        return "searchproductdocument";
    }

    /**
     * 검색어에 대한 게시글들 반환
     * 검색어에 대한 게시글들을 반환하는 메서드
     * @param search : 제목 및 내용에 대한 검색어
     * @param page : 검색어에 대한 게시글 페이지 수
     * @return : 검색어에 대한 게시글들 반환
     */
    @GetMapping("/productdocuments/searching")
    @ResponseBody
    public Slice<ProductResponseDto> getSearchProductDocuments(@RequestParam String search,
                                                               @RequestParam(required = false, defaultValue = "0") Integer page) {
        log.info("입력받은 검색어" + search);
        Slice<ProductResponseDto> productResponseDtos = productDocumentService.getSearchProductDocuments(search, page);
        return productResponseDtos;
    }

    /**
     * 게시글 생성
     * 게시글을 생성하는 메서드
     * @param productRequestDto : 생성할 게시글 내용 (title, content, price)
     * @param userDetails : 사용자에 대한 정보
     * @return : ResponseEntity에 반환URL과 상태코드를 담아 리턴
     */
    @PostMapping("/productdocuments")
    public ResponseEntity<StatusResponseDto> crateProductDocument(ProductRequestDto productRequestDto,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(userDetails.getUsername());
        String returnURL;
        try {
            returnURL = productDocumentService.createProductDocument(productRequestDto, userDetails.getUsername());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(201).body(new StatusResponseDto(returnURL, HttpStatus.CREATED.value()));
    }

    /**
     * 게시글 수정
     * 게시글을 수정하는 메서드
     * @param productRequestDto : 수정할 게시글 내용 (title, content, price)
     * @param productId : 수정할 게시글 ID
     * @param userDetails : 사용자에 대한 정보
     * @return : ResonseEntity에 반환URL과 상태코드를 담아 리턴
     */
    @PutMapping("/productdocuments/{productId}")
    public ResponseEntity<StatusResponseDto> updateProductDocument(ProductRequestDto productRequestDto,
                                                                   @PathVariable String productId,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnURL;
        try {
            returnURL = productDocumentService.updateProductDocument(productRequestDto, productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new StatusResponseDto(returnURL, HttpStatus.CREATED.value()));
    }

    /**
     * 게시글 삭제
     * 게시글을 삭제하는 메서드
     * @param productId : 삭제할 게시글의 ID
     * @param userDetails : 사용자에 대한 정보
     * @return : ResponseEntity에 반환URl과 상태코드를 담아 리턴
     */
    @DeleteMapping("/productdocuments/{productId}")
    public ResponseEntity<StatusResponseDto> deleteProductDocument(@PathVariable String productId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnURL;
        try {
            returnURL = productDocumentService.deleteProductDocument(productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok().body(new StatusResponseDto(returnURL, HttpStatus.OK.value()));
    }

}

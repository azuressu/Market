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

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductDocumentController {

    private final ProductDocumentService productDocumentService;

    // 게시글 전체 조회 페이지 반환
    @GetMapping("/productdocuments")
    public String getProductDocumentList(Model model, @RequestParam(value="page", defaultValue = "0") Integer page) {
        Page<ProductResponseDto> productResponseDtos = productDocumentService.getProductDocumentList(page);
        model.addAttribute("productdocuments", productResponseDtos);
        return "productdocuments";
    }

    // 게시글 작성 or 수정 페이지 반환
    @GetMapping("/productdocument")
    public String addproductdocumentPage(@RequestParam(required=false) String productId, Model model) {
        log.info(productId);
        // productId 이 있으면 MODEL에 데이터를 담아서 반환
        if (productId != null) {
            ProductsResponseDto productResponseDto = productDocumentService.getUpdateOneProductDocument(productId);
            model.addAttribute("product", productResponseDto);
        } else {
            model.addAttribute("product", new ProductResponseDto());
        }
        return "addproductdocument";
    }


    // 게시글 단건 조회
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

    // 게시글 검색 페이지
    @GetMapping("/productdocuments/search")
    public String getSearchProductDocumentsPage(@RequestParam String search) {
        log.info("입력받은 검색어 : " + search);
        return "searchproductdocument";
    }

    // 검색어에 대한 게시글 Slice 해서 반환
    @GetMapping("/productdocuments/searching")
    @ResponseBody
    public Slice<ProductResponseDto> getSearchProductDocuments(@RequestParam String search,
                                                               @RequestParam(required = false, defaultValue = "0") Integer page) {
        log.info("입력받은 검색어" + search);
        Slice<ProductResponseDto> productResponseDtos = productDocumentService.getSearchProductDocuments(search, page);
        return productResponseDtos;
    }

    // 게시물 생성
    @PostMapping("/productdocuments")
    public ResponseEntity<StatusResponseDto> crateProductDocument(ProductRequestDto productRequestDto,
                                                                  @AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info(userDetails.getUsername());
        String returnStatus;
        try {
            returnStatus = productDocumentService.createProductDocument(productRequestDto, userDetails.getUsername());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.status(201).body(new StatusResponseDto(returnStatus, HttpStatus.CREATED.value()));
    }

    // 게시물 수정
    @PutMapping("/productdocuments/{productId}")
    public ResponseEntity<StatusResponseDto> updateProductDocument(ProductRequestDto productRequestDto,
                                                                   @PathVariable String productId,
                                                                   @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnStatus;
        try {
            returnStatus = productDocumentService.updateProductDocument(productRequestDto, productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity.ok().body(new StatusResponseDto(returnStatus, HttpStatus.CREATED.value()));
    }

    // 게시물 삭제
    @DeleteMapping("/productdocuments/{productId}")
    public ResponseEntity<StatusResponseDto> deleteProductDocument(@PathVariable String productId,
                                                            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        String returnStatus;
        try {
            returnStatus = productDocumentService.deleteProductDocument(productId, userDetails);
        } catch (UsernameNotFoundException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(new StatusResponseDto(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
        return ResponseEntity.ok().body(new StatusResponseDto(returnStatus, HttpStatus.OK.value()));
    }

}

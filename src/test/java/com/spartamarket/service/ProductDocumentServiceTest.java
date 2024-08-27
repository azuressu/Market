package com.spartamarket.service;


import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductsResponseDto;
import com.spartamarket.entity.ProductDocument;
import com.spartamarket.entity.User;
import com.spartamarket.entity.UserRoleEnum;
import com.spartamarket.jwt.UserDetailsImpl;
import com.spartamarket.repository.ProductDocumentRepository;
import com.spartamarket.repository.ProductRepository;
import com.spartamarket.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ProductDocumentServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductDocumentRepository productDocumentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ProductDocumentService productDocumentService ;

    @Test
    @BeforeEach
    void init() {
        productDocumentService = new ProductDocumentService(
                productDocumentRepository, userRepository
        );
    }

    @Test
    @DisplayName("게시글 생성")
    public void ProductDocument_Create() {
        // given
        User user = makeUser("imuser");
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("포카 사세요");
        productRequestDto.setContent("중복 포토카드 판매합니다");
        productRequestDto.setPrice(5000);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        String result = productDocumentService.createProductDocument(productRequestDto, "imuser");

        // then
        assert result.startsWith("/api/productdocuments/");
    }

    @Test
    @DisplayName("게시글 생성 실패 - 찾을 수 없는 사용자")
    public void ProductDocument_CreateFailNotFoundUser() {
        // given
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("포카 사세요");
        productRequestDto.setContent("중복 포토카드 판매합니다");
        productRequestDto.setPrice(5000);

        // when & then
        assertThrows(UsernameNotFoundException.class, () -> {
            productDocumentService.createProductDocument(productRequestDto, "imuser");
        });
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void ProductDocument_Read() {
        // given
        User user = makeUser("imuser");
        ProductDocument productDocument = makeProductDocument(user);
        ProductDocument spyDocument = spy(productDocument);

        // when
        when(productDocumentRepository.findById(any(String.class))).thenReturn(Optional.of(spyDocument));
        when(spyDocument.getCreatedAt()).thenReturn(LocalDateTime.now());
        ProductsResponseDto productsResponseDto = productDocumentService.getOneProductDocument("ID");

        // then
        assert productsResponseDto.getTitle().equals(productDocument.getTitle());
    }

    @Test
    @DisplayName("게시글 단건 조회 - 찾을 수 없는 물품")
    public void ProductDocument_ReadFailNotFoundProduct() {
        // when & then
        assertThrows(NoSuchElementException.class, () ->
                productDocumentService.getOneProductDocument("ID")
        );
    }

    @Test
    @DisplayName("게시글 수정")
    public void ProductDocument_Update() {
        // given
        User user = makeUser("imuser");
        ProductDocument productDocument = makeProductDocument(user);
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("키링 사세요");
        productRequestDto.setContent("안쓰는 키링 팝니다");
        productRequestDto.setPrice(2000);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(productDocumentRepository.findById(any(String.class))).thenReturn(Optional.of(productDocument));
        String result = productDocumentService.updateProductDocument(productRequestDto, "ID", spyUser);

        // then
        assert result.startsWith("/api/productdocuments/");
    }

    @Test
    @DisplayName("게시글 수정 실패 - 본인이 아님")
    public void ProductDocument_UpdateFailNotUser() {
        // given
        User user = makeUser("imuser");
        User newUser = makeUser("newUser");
        ProductDocument productDocument = makeProductDocument(user);
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("키링 사세요");
        productRequestDto.setContent("안쓰는 키링 팝니다");
        productRequestDto.setPrice(1000);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(newUser));
        when(productDocumentRepository.findById(any(String.class))).thenReturn(Optional.of(productDocument));

        // then
        assertThrows(IllegalArgumentException.class, () ->
                productDocumentService.updateProductDocument(productRequestDto, "ID", spyUser)
        );
    }

    @Test
    @DisplayName("게시글 삭제")
    public void ProductDocument_Delete() {
        // given
        User user = makeUser("ImUser");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        ProductDocument productDocument = makeProductDocument(user);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(productDocumentRepository.findById(any(String.class))).thenReturn(Optional.of(productDocument));

        String result = productDocumentService.deleteProductDocument("ID", spyUser);

        // then
        assert result.equals("/api/productdocuments");
    }

    @Test
    @DisplayName("게시글 삭제 실패 - 본인이 아님")
    public void ProductDocument_DeleteFailNotUser() {
        // given
        User user = makeUser("imuser");
        User newUser = makeUser("newUser");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        ProductDocument productDocument = makeProductDocument(user);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(newUser));
        when(productDocumentRepository.findById(any(String .class))).thenReturn(Optional.of(productDocument));

        // then
        assertThrows(IllegalArgumentException.class, () ->
                productDocumentService.deleteProductDocument("ID", spyUser)
        );
    }

    /**
     * 테스트 사용자 생성 메서드
     * @param username : 사용자 이름
     * @return : 생성한 사용자 반환
     */
    private User makeUser(String username) {
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername(username);
        joinRequestDto.setPassword("icecream00");
        joinRequestDto.setNickname("iceice");

        User makeUser = new User(joinRequestDto, passwordEncoder.encode(joinRequestDto.getPassword()),
                UserRoleEnum.USER);

        return makeUser;
    }

    /**
     * 테스트 게시글 생성 메서드
     * @param user : 게시글 작성자
     * @return : 생성한 게시글 반환
     */
    private ProductDocument makeProductDocument(User user) {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("안쓰는 포토카드 팔아요");
        productRequestDto.setContent("내용 보시고 원하시는 카드 알려주세요");
        productRequestDto.setPrice(1000);

        ProductDocument productDocument = new ProductDocument(productRequestDto, user);
        return productDocument;
    }

}

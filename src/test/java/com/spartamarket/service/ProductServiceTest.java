package com.spartamarket.service;

import com.spartamarket.dto.JoinRequestDto;
import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.dto.ProductsResponseDto;
import com.spartamarket.entity.Product;
import com.spartamarket.entity.User;
import com.spartamarket.entity.UserRoleEnum;
import com.spartamarket.jwt.UserDetailsImpl;
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
class ProductServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private ProductService productService;

    @Test
    @BeforeEach
    void init() {
        productService = new ProductService(
                productRepository, userRepository
        );
    }

    @Test
    @DisplayName("게시글 등록 성공")
    public void ProductTest_Create() {
        // given
        User user = makeUser("highlight");
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("굿즈 팝니다");
        productRequestDto.setContent("비스트 포카 중복된 거 팝니다");
        productRequestDto.setPrice(5000);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        String result = productService.createProduct(productRequestDto, "highlight");

        // then
        assert result.startsWith("/api/products/");
    }

    @Test
    @DisplayName("게시글 등록 실패 - 찾을 수 없는 사용자")
    public void ProductTest_CreateFailAboutUsername() {
        // given
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("굿즈 팝니다");
        productRequestDto.setContent("비스트 포카 중복된 거 팝니다");
        productRequestDto.setPrice(5000);

        // when
        Throwable exception = assertThrows(UsernameNotFoundException.class, () -> {
            productService.createProduct(productRequestDto, "highlight");
        });

        // then
        assert exception.getMessage().equals("존재하지 않는 사용자");
    }

    @Test
    @DisplayName("게시글 단건 조회")
    public void ProductTest_Read() {
        // given
        User user = makeUser("highlight");
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("굿즈 팝니다");
        productRequestDto.setContent("비스트 포카 중복된 거 팝니다");
        productRequestDto.setPrice(5000);

        Product product = new Product(productRequestDto, user);
        Product spyProduct = spy(product);

        // when
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(spyProduct));
        when(spyProduct.getCreatedAt()).thenReturn(LocalDateTime.now());
        ProductsResponseDto productResponseDto = productService.getOneProduct(1L);

        // then
        assert productResponseDto.getTitle().equals(product.getTitle());
    }

    @Test
    @DisplayName("게시글 단건 조회 실패 - 찾을 수 없는 물품")
    public void ProductTest_ReadFailAboutProductId() {
        // given & when
        Throwable exception = assertThrows(NoSuchElementException.class, () -> {
            productService.getOneProduct(1L);
        });

        // then
        exception.getMessage().equals("존재하지 않는 물품");
    }

    @Test
    @DisplayName("게시글 수정")
    public void ProductTest_UpdateProduct() {
        // given
        User user = makeUser("highlight");
        Product product = makeProduct(user);
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("키링 사세요");
        productRequestDto.setContent("안쓰는 키링 팝니다");
        productRequestDto.setPrice(1000);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        String result = productService.updateProduct(productRequestDto, 1L, spyUser);

        // then
        assert result.startsWith("/api/products/");
    }

    @Test
    @DisplayName("게시글 수정 실패 - 글 작성자가 아님")
    public void ProductTest_UpdateProductFailNotUser() {
        // given
        User user = makeUser("ImUser");
        User fakeUser = makeUser("newUser");
        Product product = makeProduct(user);
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("키링 사세요");
        productRequestDto.setContent("안쓰는 키링 팝니다");
        productRequestDto.setPrice(1000);

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(fakeUser));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProduct(productRequestDto, 1L, spyUser);
        });

        // then
        assert exception.getMessage().equals("글 작성자가 아닙니다");
    }

    @Test
    @DisplayName("게시글 삭제")
    public void ProductTest_DeleteProduct() {
        // given
        User user = makeUser("ImUser");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        Product product = makeProduct(user);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(user));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));

        String result = productService.deleteProduct(1L, spyUser);

        // then
        assert result.equals("/api/products");
    }

    @Test
    @DisplayName("게시글 삭제 실패")
    public void ProductTest_DeleteProductFailNotUser() {
        // given
        User user = makeUser("ImUser");
        User fakeUser = makeUser("newUser");
        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        UserDetailsImpl spyUser = spy(userDetails);

        Product product = makeProduct(user);

        // when
        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(fakeUser));
        when(productRepository.findById(any(Long.class))).thenReturn(Optional.of(product));
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.deleteProduct(1L, spyUser);
        });

        // then
        assert exception.getMessage().equals("글 작성자가 아닙니다");
    }

    /**
     * 테스트를 위한 사용자 생성 메서드
     * @return : 생성한 사용자 반환
     */
    public User makeUser(String username) {
        JoinRequestDto joinRequestDto = new JoinRequestDto();
        joinRequestDto.setUsername(username);
        joinRequestDto.setPassword("highlight00");
        joinRequestDto.setNickname("light");

        User makeUser = new User(joinRequestDto, passwordEncoder.encode(joinRequestDto.getPassword()),
                UserRoleEnum.USER);

        return makeUser;
    }

    /**
     * 테스트를 위한 게시글 생성 메서드
     * @param user : 게시글 작성자
     * @return : 생성한 게시글 빈환
     */
    public Product makeProduct(User user) {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle("굿즈 팝니다");
        productRequestDto.setContent("비스트 포카 중복된 거 팝니다");
        productRequestDto.setPrice(5000);

        Product product = new Product(productRequestDto, user);
        return product;
    }

}
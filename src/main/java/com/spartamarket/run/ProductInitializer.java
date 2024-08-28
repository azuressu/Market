package com.spartamarket.run;

import com.spartamarket.dto.ProductRequestDto;
import com.spartamarket.entity.Product;
import com.spartamarket.entity.User;
import com.spartamarket.repository.ProductRepository;
import com.spartamarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;


@Component
@RequiredArgsConstructor
public class ProductInitializer implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<Integer, String> titles = ProductData.TITLES.getProductData();
        Map<Integer, String> contents = ProductData.CONTENTS.getProductData();

        List<Product> productList = new ArrayList<>();

        for (int i=1; i<101; i++) {
            User writer = findUser();
            ProductRequestDto productRequestDto = makeProductRequest(titles.get(i), contents.get(i));
            Product product = new Product(productRequestDto,writer);

            productList.add(product);
        }

        productRepository.saveAll(productList);
    }

    private User findUser() {
        Long randomId = ThreadLocalRandom.current().nextLong(1, 31);
        Optional<User> randomUser = userRepository.findById(randomId);
        return randomUser.orElseThrow();
    }

    private ProductRequestDto makeProductRequest(String title, String content) {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        productRequestDto.setTitle(title);
        productRequestDto.setContent(content);
        productRequestDto.setPrice(1000);
        return productRequestDto;
    }
}

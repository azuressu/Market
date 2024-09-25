package com.mymarket.run;

import com.mymarket.dto.ProductRequestDto;
import com.mymarket.entity.ProductDocument;
import com.mymarket.entity.User;
import com.mymarket.repository.ProductDocumentRepository;
import com.mymarket.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class ProductDocumentInitializer implements ApplicationRunner {

    private final ProductDocumentRepository productDocumentRepository;
    private final UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Map<Integer, String> titles = ProductData.TITLES.getProductData();
        Map<Integer, String> contents = ProductData.CONTENTS.getProductData();

        List<ProductDocument> productDocumentList = new ArrayList<>();

        for (int i=1; i<101; i++) {
            User writer = findUser();
            ProductRequestDto productRequestDto = makeProductRequest(titles.get(i), contents.get(i));
            ProductDocument productDocument = new ProductDocument(productRequestDto, writer);

            productDocumentList.add(productDocument);
        }

        productDocumentRepository.saveAll(productDocumentList);
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


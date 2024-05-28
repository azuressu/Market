package com.spartamarket.entity;

import co.elastic.clients.util.DateTime;
import com.spartamarket.dto.ProductRequestDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "product")
public class ProductDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "content", type = FieldType.Text)
    private String content;

    @Field(name = "price", type = FieldType.Integer)
    private Integer price;

    @Field(name = "user")
    private User user;

    @Field(name = "created_at", type = FieldType.Date)
    private LocalDateTime createAt;

    @Field(name = "updated_at", type = FieldType.Date)
    private LocalDateTime updatedAt;

    public ProductDocument(ProductRequestDto productRequestDto, User user,
                           LocalDateTime createAt, LocalDateTime updatedAt) {
        this.user = user;
        this.title = productRequestDto.getTitle();
        this.content = productRequestDto.getContent();
        this.price = productRequestDto.getPrice();
        this.createAt = createAt;
        this.updatedAt = updatedAt;
    }


}

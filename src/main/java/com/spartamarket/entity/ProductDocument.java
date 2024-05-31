package com.spartamarket.entity;

import co.elastic.clients.util.DateTime;
import com.spartamarket.dto.ProductRequestDto;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import com.spartamarket.entity.User;
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
    private String id;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "content", type = FieldType.Text)
    private String content;

    @Field(name = "price", type = FieldType.Integer)
    private Integer price;

    /*@Field(name = "user", type = FieldType.Integer_Range)
    private User user;*/
    @Field(name = "username", type = FieldType.Text)
    private String username;

    @Field(name = "created_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;

    @Field(name = "updated_at", type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime updatedAt;

    public ProductDocument(ProductRequestDto productRequestDto, User user,
                           LocalDateTime createAt, LocalDateTime updatedAt) {
        this.username = user.getUsername();
        this.title = productRequestDto.getTitle();
        this.content = productRequestDto.getContent();
        this.price = productRequestDto.getPrice();
        this.createdAt = createAt;
        this.updatedAt = updatedAt;
    }


}

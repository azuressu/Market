package com.mymarket.entity;

import com.mymarket.dto.ProductRequestDto;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Document(indexName = "productdocuments")
public class ProductDocument extends ElasticsearchTimestamped {

    @Id
    private String id;

    @Field(name = "title", type = FieldType.Text)
    private String title;

    @Field(name = "content", type = FieldType.Text)
    private String content;

    @Field(name = "price", type = FieldType.Integer)
    private Integer price;

    @Field(name = "user_id")
    private User user;

    public ProductDocument(ProductRequestDto productRequestDto, User user) {
        this.user = user;
        this.title = productRequestDto.getTitle();
        this.content = productRequestDto.getContent();
        this.price = productRequestDto.getPrice();
    }

    public void updateProductDocument(ProductRequestDto productRequestDto) {
        this.title = productRequestDto.getTitle();
        this.content = productRequestDto.getContent();
        this.price = productRequestDto.getPrice();
    }

}

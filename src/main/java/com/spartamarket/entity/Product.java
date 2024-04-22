package com.spartamarket.entity;

import com.spartamarket.dto.ProductRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

import com.spartamarket.entity.User;

// lombok
@Getter
@NoArgsConstructor

// jpa
@Entity
@Table(name = "products")
public class Product extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "price", nullable = false)
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Product(ProductRequestDto productRequestDto, User user) {
        this.title = productRequestDto.getTitle();
        this.content = productRequestDto.getContent();
        this.price = productRequestDto.getPrice();
        this.user = user;
    }
}

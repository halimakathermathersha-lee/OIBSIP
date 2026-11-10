package com.halima.library.model;

import jakarta.persistence.*;

@Entity
public class Book {
    @Column(name = "book_id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long bid;
    String title,
            author,
            isbn,
            category;
    Boolean available = true;

    public Long getBid() {
        return bid;
    }

    public void setBid(Long x) {
        bid = x;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String x) {
        title = x;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String x) {
        author = x;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String x) {
        isbn = x;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String x) {
        category = x;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean x) {
        available = x;
    }
}

package com.halima.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name, email, message;
    LocalDate createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long x) {
        id = x;
    }

    public String getName() {
        return name;
    }

    public void setName(String x) {
        name = x;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String x) {
        email = x;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String x) {
        message = x;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate x) {
        createdAt = x;
    }
}

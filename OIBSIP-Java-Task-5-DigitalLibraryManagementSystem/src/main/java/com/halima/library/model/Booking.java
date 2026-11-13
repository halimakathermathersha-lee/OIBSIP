package com.halima.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long bookId, memberId;
    String bookTitle, status;
    LocalDate bookingDate;

    public Long getId() {
        return id;
    }

    public void setId(Long x) {
        id = x;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long x) {
        bookId = x;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long x) {
        memberId = x;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String x) {
        bookTitle = x;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String x) {
        status = x;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate x) {
        bookingDate = x;
    }
}

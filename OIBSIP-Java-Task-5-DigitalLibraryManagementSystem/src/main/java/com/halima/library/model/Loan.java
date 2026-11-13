package com.halima.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    Long bookId, memberId;
    String bookTitle, memberName;
    LocalDate issueDate, dueDate, returnDate;
    boolean returned;

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

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String x) {
        memberName = x;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate x) {
        issueDate = x;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate x) {
        dueDate = x;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate x) {
        returnDate = x;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean x) {
        returned = x;
    }

    public long getFine() {
        return returned || dueDate == null ? 0 : Math.max(0, ChronoUnit.DAYS.between(dueDate, LocalDate.now())) * 5;
    }
}

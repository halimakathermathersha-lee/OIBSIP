package com.halima.library.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Member{
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long id;
    String name,email,phone;
    public Long getId(){return id;}
    public void setId(Long x){id=x;}
    public String getName(){return name;}
    public void setName(String x){name=x;}
    public String getEmail(){return email;}
    public void setEmail(String x){email=x;}
    public String getPhone(){return phone;}
    public void setPhone(String x){phone=x;}}


package com.halima.library.repository;


import com.halima.library.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

 public interface BookingRepo extends JpaRepository<Booking, Long> {
}

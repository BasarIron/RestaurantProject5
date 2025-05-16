package com.grup7.Repository;

import com.grup7.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IOrderRepository  extends JpaRepository<Order, Long> {
    Optional<Order> findByReservationCode(String reservationCode);
}

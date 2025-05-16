package com.grup7.Repository;

import com.grup7.Entity.OrderLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderLogRepository extends JpaRepository<OrderLog, Long> {
}
package com.grup7.Repository;

import com.grup7.Entity.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ITableRepository extends JpaRepository<Table, Long> {
        List<Table> findByReservedDatesNotContaining(LocalDate date);
}
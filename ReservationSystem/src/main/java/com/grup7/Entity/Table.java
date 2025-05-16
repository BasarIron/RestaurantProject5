package com.grup7.Entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@jakarta.persistence.Table(name = "tables")
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableNumber;

    @ElementCollection
    @CollectionTable(name = "table_reservations",
            joinColumns = @JoinColumn(name = "table_id"))
    private List<LocalDate> reservedDates;
}